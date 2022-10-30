package com.theyugin.nee.component;

import static com.theyugin.nee.LoadedMods.GREGTECH;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.theyugin.nee.NotEnoughExports;
import com.theyugin.nee.component.export.*;
import com.theyugin.nee.component.service.ServiceModule;
import com.theyugin.nee.util.NEEUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.val;
import net.minecraft.util.EnumChatFormatting;

public class ExporterRunner {
    public static Thread exporterThread = null;

    public static void runExport() {
        isRunning = true;
        val start = System.nanoTime();
        startRunning();
        try {
            for (IExporter exporter : loadedExporters) {
                exporter.run();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            stopRunning();
            val total = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start);
            NEEUtils.sendPlayerMessage(
                    EnumChatFormatting.GREEN + String.format("Successfully exported in %d seconds!", total));
            loadedExporters = new ArrayList<>();
            injector = null;
            isRunning = false;
        }
    }

    public static boolean exporting() {
        return exporterThread != null && exporterThread.isAlive();
    }

    public static List<IExporter> loadedExporters = new ArrayList<>();
    private static boolean isRunning = false;

    public static synchronized void startRunning() {
        isRunning = true;
    }

    public static synchronized void stopRunning() {
        isRunning = false;
    }

    public static synchronized boolean isRunning() {
        return isRunning;
    }

    private static Injector injector;

    private static void initDb() {
        val db = new File("nee.sqlite3");
        if (db.exists()) {
            db.delete();
        }
        try (val conn = NEEUtils.createConnection();
                val is = Thread.currentThread().getContextClassLoader().getResourceAsStream("def.sql")) {
            val statementBuffer = new StringBuilder();
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

    public static void run() {
        initDb();
        injector = Guice.createInjector(new ComponentModule(), new ServiceModule(), new ExportModule());
        loadedExporters.add(injector.getInstance(CatalystExporter.class));
        loadedExporters.add(injector.getInstance(CraftingTableExporter.class));
        if (GREGTECH.isLoaded()) loadedExporters.add(injector.getInstance(GregTechExporter.class));
        exporterThread = new Thread(ExporterRunner::runExport);
        exporterThread.start();
    }
}
