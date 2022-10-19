package com.theyugin.nee;

import com.theyugin.nee.export.CraftingTableExporter;
import com.theyugin.nee.export.GregTechExporter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class ExporterRunner implements Runnable {
    private void populateDatabase(Connection conn) throws SQLException {
        StringBuilder statementBuffer = new StringBuilder();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("dbdef.sql")) {
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

    public void run() {
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.enforceForeignKeys(true);
        sqLiteConfig.setJournalMode(SQLiteConfig.JournalMode.WAL);
        sqLiteConfig.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        sqLiteConfig.setTempStore(SQLiteConfig.TempStore.MEMORY);

        SQLiteDataSource ds = new SQLiteDataSource(sqLiteConfig);

        File dbFile = new File("nee.sqlite");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        ds.setUrl("jdbc:sqlite:nee.sqlite");

        try (Connection conn = ds.getConnection()) {
            populateDatabase(conn);
            CraftingTableExporter.run(conn);
            GregTechExporter.run(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
