package com.theyugin.nee.sql;

import com.theyugin.nee.data.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemBuilder implements IDataBuilder<Item> {
    private String unlocalizedName;
    private String localizedName;

    private byte[] icon;

    public ItemBuilder setUnlocalizedName(String name) {
        unlocalizedName = name;
        return this;
    }

    public ItemBuilder setLocalizedName(String name) {
        localizedName = name;
        return this;
    }

    public ItemBuilder setIcon(byte[] data) {
        this.icon = data;
        return this;
    }

    public Item save(Connection conn) throws SQLException {
        if (unlocalizedName == null || localizedName == null) {
            throw new SQLException("Unset parameters");
        }
        PreparedStatement stmt = conn.prepareStatement(
                "insert or ignore into item (unlocalizedName, localizedName, icon) values (?, ?, ?)");
        stmt.setString(1, this.unlocalizedName);
        stmt.setString(2, this.localizedName);
        if (icon != null) {
            stmt.setBytes(3, icon);
        }
        stmt.executeUpdate();
        return new Item(unlocalizedName, localizedName);
    }
}
