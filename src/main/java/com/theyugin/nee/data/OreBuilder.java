package com.theyugin.nee.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OreBuilder implements IDataBuilder<Ore> {
    private final String name;
    private final List<Item> items = new ArrayList<>();

    private OreBuilder(String name) {
        this.name = name;
    }

    public static OreBuilder fromName(String name) {
        return new OreBuilder(name);
    }

    public OreBuilder addItem(Item item) {
        this.items.add(item);
        return this;
    }

    public Ore save(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert or ignore into ore (name) values (?)");
        stmt.setString(1, this.name);
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
