package com.theyugin.nee.component;

import static com.theyugin.nee.LoadedMods.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.theyugin.nee.Config;
import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.component.export.*;
import com.theyugin.nee.component.service.ServiceModule;
import com.theyugin.nee.render.StackRenderer;
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
    private Injector injector;
    private Connection conn;

    @SneakyThrows
    private void runExport() {
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
            injector.getInstance(Connection.class).close();
            injector = null;
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
        }
    }

    private void initDb() {
        try (val conn = NEEUtils.createConnection();
                val is = Thread.currentThread().getContextClassLoader().getResourceAsStream("def.sql")) {
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

    public void run() {
        initDb();
        StackRenderer.initialize();
        injector = Guice.createInjector(new ComponentModule(), new ServiceModule(), new ExportModule());
        if (Config.exportCatalysts()) loadedExporters.add(injector.getInstance(CatalystExporter.class));
        if (Config.exportCraftingTable()) loadedExporters.add(injector.getInstance(CraftingTableExporter.class));
        if (Config.exportGregtech() && GREGTECH.isLoaded())
            loadedExporters.add(injector.getInstance(GregTechExporter.class));
        if (Config.exportThaumcraft() && THAUMCRAFT.isLoaded())
            loadedExporters.add(injector.getInstance(ThaumcraftExporter.class));
        conn = injector.getInstance(Connection.class);
        runExport();
        StackRenderer.uninitialize();
    }
}
