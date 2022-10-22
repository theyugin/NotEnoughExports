package com.theyugin.nee;

import static com.theyugin.nee.LoadedMods.GREGTECH;

import com.theyugin.nee.export.CatalystExporter;
import com.theyugin.nee.export.CraftingTableExporter;
import com.theyugin.nee.export.GregTechExporter;
import com.theyugin.nee.export.IExporter;
import com.theyugin.nee.util.NEEUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gtPlusPlus.core.client.renderer.CustomOreBlockRenderer;
import net.minecraft.util.EnumChatFormatting;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class ExporterRunner implements Runnable {
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

    private void populateDatabase(Connection conn) throws SQLException {
        StringBuilder statementBuffer = new StringBuilder();
        try (InputStream is =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/nee/sql/dbdef.sql")) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                statementBuffer.append(line);
            }
            bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Statement stmt = conn.createStatement();
        for (String s : statementBuffer.toString().split(";")) {
            stmt.addBatch(s);
        }

        stmt.executeBatch();
    }

    private void loadExporters(Connection conn) {
        loadedExporters.add(new CatalystExporter(conn));
        loadedExporters.add(new CraftingTableExporter(conn));
        if (GREGTECH.isLoaded()) loadedExporters.add(new GregTechExporter(conn));
    }

    public void run() {
        long start = System.nanoTime();
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.enforceForeignKeys(true);
        sqLiteConfig.setJournalMode(SQLiteConfig.JournalMode.WAL);
        sqLiteConfig.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        sqLiteConfig.setTempStore(SQLiteConfig.TempStore.MEMORY);

        SQLiteDataSource ds = new SQLiteDataSource(sqLiteConfig);

        for (File file :
                Arrays.asList(new File("nee.sqlite"), new File("nee.sqlite-shm"), new File("nee.sqlite-wal"))) {
            if (file.exists()) file.delete();
        }
        ds.setUrl("jdbc:sqlite:nee.sqlite");

        try (Connection conn = ds.getConnection()) {
            populateDatabase(conn);
            loadExporters(conn);
            startRunning();
            for (IExporter exporter : loadedExporters) {
                exporter.run();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        stopRunning();
        loadedExporters = new ArrayList<>();
        long total = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start);
        NEEUtils.sendPlayerMessage(
                EnumChatFormatting.GREEN + String.format("Successfully exported in %d seconds!", total));
    }
}
