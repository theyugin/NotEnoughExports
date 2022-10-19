package com.theyugin.nee.sql;

import com.theyugin.nee.data.Item;
import com.theyugin.nee.data.Ore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OreBuilder implements IDataBuilder<Ore> {
    private String name;
    private final List<Item> items = new ArrayList<>();

    public OreBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public OreBuilder addItem(Item item) {
        this.items.add(item);
        return this;
    }

    public Ore save(Connection conn) throws SQLException {
        if (this.name == null) {
            throw new SQLException("unset parameters");
        }
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
