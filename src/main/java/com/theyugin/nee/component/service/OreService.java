package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.persistence.general.Ore;
import com.theyugin.nee.persistence.general.OreItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

@Singleton
public class OreService extends AbstractCacheableService<Ore> {
    private final Connection conn;
    private final Set<OreItem> oreItemCache = new HashSet<>();
    private final PreparedStatement insertOreStmt;
    private final PreparedStatement insertOreItemStmt;

    @Inject
    @SneakyThrows
    public OreService(@NonNull Connection conn) {
        this.conn = conn;
        insertOreStmt = conn.prepareStatement("insert or ignore into ore (name) values (?)");
        insertOreItemStmt = conn.prepareStatement(
                "insert or ignore into ore_item (item_registry_name, item_nbt, ore_name) values (?, ?, ?)");
    }

    @SneakyThrows
    public Ore createOrGet(String name) {
        val ore = Ore.builder().name(name).build();
        if (putInCache(ore)) {
            return ore;
        }
        insertOreStmt.setString(1, name);
        insertOreStmt.executeUpdate();
        return ore;
    }

    @SneakyThrows
    public void addItem(Ore ore, Item item) {
        val oreItem = OreItem.builder().ore(ore).item(item).build();
        if (oreItemCache.contains(oreItem)) {
            return;
        }
        oreItemCache.add(oreItem);
        insertOreItemStmt.setString(1, item.getRegistryName());
        insertOreItemStmt.setString(2, item.getNbt());
        insertOreItemStmt.setString(3, ore.getName());
        insertOreItemStmt.executeUpdate();
    }
}
