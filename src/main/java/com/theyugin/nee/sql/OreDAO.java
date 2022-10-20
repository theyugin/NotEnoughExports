package com.theyugin.nee.sql;

import com.theyugin.nee.data.Item;
import com.theyugin.nee.data.Ore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class OreDAO extends DAO {
    public OreDAO(Connection conn) {
        super(conn);
    }

    public Ore create(String name, Set<Item> items) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert or ignore into ore (name) values (?)");
        stmt.setString(1, name);
        stmt.executeUpdate();

        stmt = conn.prepareStatement("insert or ignore into oreItem (item, name) values (?, ?)");
        for (Item item : items) {
            stmt.setString(1, item.unlocalizedName);
            stmt.setString(2, name);
            stmt.addBatch();
            stmt.clearParameters();
        }
        stmt.executeBatch();
        return new Ore(name);
    }
}
