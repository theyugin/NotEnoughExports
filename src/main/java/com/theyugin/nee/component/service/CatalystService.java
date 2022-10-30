package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Catalyst;
import com.theyugin.nee.persistence.general.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import lombok.var;

@Singleton
public class CatalystService {
    private final PreparedStatement catalystStmt;
    private final PreparedStatement catalystItemStmt;
    private final Set<Catalyst> catalystCache = new HashSet<>();

    @Inject
    public CatalystService(@NonNull Connection conn) throws SQLException {
        catalystStmt = conn.prepareStatement("insert or ignore into catalyst (name) values (?)");
        catalystItemStmt = conn.prepareStatement(
                "insert or ignore into catalyst_item (catalyst_name, item_registry_name) values (?, ?)");
    }

    public Catalyst getOrCreate(String name) throws SQLException {
        var catalyst = Catalyst.builder().name(name).build();
        if (catalystCache.contains(catalyst)) {
            return catalyst;
        }
        catalystCache.add(catalyst);
        catalystStmt.setString(1, name);
        catalystStmt.executeUpdate();
        return catalyst;
    }

    public void addItem(Catalyst catalyst, Item item) throws SQLException {
        catalystItemStmt.setString(1, catalyst.getName());
        catalystItemStmt.setString(2, item.getRegistryName());
        catalystItemStmt.executeUpdate();
    }
}
