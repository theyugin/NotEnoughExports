package com.theyugin.nee.util;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class NEEUtils {
    public static void sendPlayerMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static Connection createConnection() throws SQLException {
        val config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.enforceForeignKeys(true);
        val ds = new SQLiteDataSource(config);
        ds.setUrl("jdbc:sqlite:nee.sqlite3");
        ds.getConnection();
        return ProxyDataSourceBuilder.create(ds).countQuery().build().getConnection();
    }
}
