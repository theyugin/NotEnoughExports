package com.theyugin.nee.sql;

import com.theyugin.nee.data.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemDAO extends DAO {

    public ItemDAO(Connection conn) {
        super(conn);
    }

    public Item get(String unlocalizedName) throws SQLException {
        PreparedStatement stmt =
                conn.prepareStatement("select unlocalizedName, localizedName from item where unlocalizedName = ?");
        stmt.setString(1, unlocalizedName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Item(rs.getString(1), rs.getString(2));
        }
        return null;
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
