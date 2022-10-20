package com.theyugin.nee.sql;

import com.theyugin.nee.data.CatalystType;
import com.theyugin.nee.data.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class CatalystTypeDAO extends DAO {
    public CatalystTypeDAO(Connection conn) {
        super(conn);
    }

    public CatalystType create(String name) throws SQLException {
        return create(name, new HashSet<>());
    }

    public CatalystType create(String name, Set<Item> items) throws SQLException {
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
