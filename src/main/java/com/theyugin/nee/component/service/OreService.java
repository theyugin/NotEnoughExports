package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.persistence.general.Ore;
import com.theyugin.nee.persistence.general.OreItem;
import java.sql.Connection;
import java.util.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

@Singleton
public class OreService extends AbstractCacheableService<Ore> {
    private final Connection conn;
    private final Set<OreItem> oreItemCache = new HashSet<>();

    @Inject
    public OreService(@NonNull Connection conn) {
        this.conn = conn;
    }

    @SneakyThrows
    public Ore createOrGet(String name) {
        val ore = Ore.builder().name(name).build();
        if (putInCache(ore)) {
            return ore;
        }
        val stmt = conn.prepareStatement("insert or ignore into ore (name) values (?)");
        stmt.setString(1, name);
        stmt.executeUpdate();
        return ore;
    }

    @SneakyThrows
    public void addItem(Ore ore, Item item) {
        val oreItem = OreItem.builder().ore(ore).item(item).build();
        if (oreItemCache.contains(oreItem)) {
            return;
        }
        val stmt = conn.prepareStatement("insert or ignore into ore_item (item_registry_name, ore_name) values (?, ?)");
        stmt.setString(1, item.getRegistryName());
        stmt.setString(2, ore.getName());
        stmt.executeUpdate();
        oreItemCache.add(oreItem);
    }
}
