package com.theyugin.nee.sql;

import com.theyugin.nee.data.CatalystType;
import com.theyugin.nee.data.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class CatalystTypeBuilder implements IDataBuilder<CatalystType> {
    private String name;
    private Set<Item> items = new HashSet<>();

    public CatalystTypeBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CatalystTypeBuilder addItem(Item item) {
        this.items.add(item);
        return this;
    }

    public CatalystType save(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert or ignore into catalystType (name) values (?)");
        stmt.setString(1, name);
        stmt.executeUpdate();

        if (!items.isEmpty()) {
            stmt = conn.prepareStatement("insert or ignore into catalystTypeItem (name, item) values (?, ?)");
            for (Item item : items) {
                stmt.setString(1, name);
                stmt.setString(2, item.unlocalizedName);
                stmt.addBatch();
                stmt.clearParameters();
            }
            stmt.executeBatch();
        }

        return new CatalystType(name);
    }
}
