package com.theyugin.nee.sql;

import com.theyugin.nee.data.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemDAO extends DAO {

    public ItemDAO(Connection conn) {
        super(conn);
    }

    public Item create(String unlocalizedName, String localizedName, byte[] icon) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "insert or ignore into item (unlocalizedName, localizedName, icon) values (?, ?, ?)");
        stmt.setString(1, unlocalizedName);
        stmt.setString(2, localizedName);
        stmt.setBytes(3, icon);
        stmt.executeUpdate();
        return new Item(unlocalizedName, localizedName);
    }
}
