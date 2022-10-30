package com.theyugin.nee.util;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class NEEUtils {
    public static void sendPlayerMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static Connection createConnection() throws SQLException {
        val config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        val ds = new SQLiteDataSource(config);
        ds.setUrl("jdbc:sqlite:nee.sqlite3");
        return ds.getConnection();
    }
}
