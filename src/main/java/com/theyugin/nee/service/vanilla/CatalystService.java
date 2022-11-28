package com.theyugin.nee.service.vanilla;

import com.theyugin.nee.data.general.Catalyst;
import com.theyugin.nee.data.general.Item;
import com.theyugin.nee.service.AbstractCacheableService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.var;

public class CatalystService extends AbstractCacheableService<Catalyst> {
    private final PreparedStatement catalystStmt;
    private final PreparedStatement catalystItemStmt;

    @SneakyThrows
    public CatalystService(Connection conn) {
        catalystStmt = conn.prepareStatement("insert or ignore into catalyst (name) values (?)");
        catalystItemStmt = conn.prepareStatement(
                "insert or ignore into catalyst_item (catalyst_name, item_registry_name, item_nbt) values (?, ?, ?)");
    }

    @SneakyThrows
    public Catalyst getOrCreate(String name) {
        var catalyst = new Catalyst(name);
        if (putInCache(catalyst)) {
            return catalyst;
        }
        catalystStmt.setString(1, name);
        catalystStmt.executeUpdate();
        return catalyst;
    }

    @SneakyThrows
    public void addItem(Catalyst catalyst, Item item) {
        catalystItemStmt.setString(1, catalyst.getName());
        catalystItemStmt.setString(2, item.getRegistryName());
        catalystItemStmt.setString(3, item.getNbt());
        catalystItemStmt.executeUpdate();
    }
}
