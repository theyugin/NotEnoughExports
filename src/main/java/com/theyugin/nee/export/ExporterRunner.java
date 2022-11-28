package com.theyugin.nee.export;

import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.config.ExportConfigOption;
import com.theyugin.nee.export.exporter.*;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.service.vanilla.VanillaRecipeService;
import com.theyugin.nee.service.general.FluidService;
import com.theyugin.nee.service.general.ItemService;
import com.theyugin.nee.service.general.OreService;
import com.theyugin.nee.service.gregtech.GregtechRecipeService;
import com.theyugin.nee.service.thaumcraft.AspectService;
import com.theyugin.nee.service.thaumcraft.ThaumcraftRecipeService;
import com.theyugin.nee.service.vanilla.CatalystService;
import com.theyugin.nee.util.NEEUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.util.EnumChatFormatting;
import net.ttddyy.dsproxy.QueryCountHolder;

public class ExporterRunner {
    public List<AbstractExporter> loadedExporters = new ArrayList<>();

    @SneakyThrows
    private void runExport(Connection conn) {
        val start = System.nanoTime();
        conn.setAutoCommit(false);
        try {
            for (AbstractExporter exporter : loadedExporters) {
                exporter.run();
            }
        } finally {
            conn.commit();
            NotEnoughExports.info(String.format(
                    "Executed %d queries in %d ms",
                    QueryCountHolder.getGrandTotal().getTotal(),
                    QueryCountHolder.getGrandTotal().getTime()));
            QueryCountHolder.clear();
            val total = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start);
            NEEUtils.sendPlayerMessage(
                    EnumChatFormatting.GREEN + String.format("Successfully exported in %d seconds!", total));
            loadedExporters = new ArrayList<>();
        }
    }

    private void initDb(Connection conn) {
        try (val is = Thread.currentThread().getContextClassLoader().getResourceAsStream("def.ddl")) {
            conn.createStatement().execute("pragma writable_schema = 1");
            conn.createStatement().execute("delete from sqlite_master where type in ('table', 'index', 'trigger')");
            conn.createStatement().execute("pragma writable_schema = 0");
            conn.createStatement().execute("vacuum");
            val statementBuffer = new StringBuilder();
            assert is != null;
            val bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                statementBuffer.append(line);
            }
            bufferedReader.readLine();

            val stmt = conn.createStatement();
            for (String s : statementBuffer.toString().split(";")) {
                stmt.addBatch(s);
            }

            stmt.executeBatch();
        } catch (IOException | SQLException e) {
            NotEnoughExports.error(e.getMessage());
        }
    }

    @SneakyThrows
    public void run() {
        try (val conn = NEEUtils.createConnection()) {
            initDb(conn);
            conn.setAutoCommit(false);
            StackRenderer.initialize();
            val itemService = new ItemService(conn);
            val catalystService = new CatalystService(conn);
            val oreService = new OreService(conn, itemService);
            val fluidService = new FluidService(conn);
            if (ExportConfigOption.CATALYSTS.get()) {
                loadedExporters.add(new CatalystExporter(catalystService, itemService));
            }
            if (ExportConfigOption.VANILLA.get()) {
                loadedExporters.add(new VanillaExporter(itemService, oreService, new VanillaRecipeService(conn)));
            }
            if (ExportConfigOption.GREGTECH.get()) {
                val gtRecipeService = new GregtechRecipeService(conn);
                val gtExporter = new GregTechExporter(gtRecipeService, itemService, fluidService, catalystService);
                loadedExporters.add(gtExporter);
                if (ExportConfigOption.GTPLUSPLUS.get()) {
                    loadedExporters.add(new GTPlusPlusExporter(gtExporter, gtRecipeService));
                }
            }
            if (ExportConfigOption.THAUMCRAFT.get()) {
                loadedExporters.add(new ThaumcraftExporter(
                        new AspectService(conn), itemService, oreService, new ThaumcraftRecipeService(conn)));
            }
            runExport(conn);
            conn.commit();
            StackRenderer.uninitialize();
        }
    }
}
