package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.render.RenderQuery;
import com.theyugin.nee.render.RenderState;
import com.theyugin.nee.render.RenderType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import lombok.val;
import net.minecraft.item.ItemStack;

@Singleton
public class ItemService {
    private final Set<Item> cache = new HashSet<>();
    private final PreparedStatement insertStmt;

    @Inject
    public ItemService(@NonNull Connection conn) throws SQLException {
        insertStmt = conn.prepareStatement(
                "insert or ignore into item (registry_name, display_name, icon) values (?, ?, ?)");
    }

    public Item processItemStack(@NonNull ItemStack itemStack) throws SQLException {
        val itemRegistryName = net.minecraft.item.Item.itemRegistry.getNameForObject(itemStack.getItem());
        int metadata = itemStack.getItemDamage();
        val registryName = String.format("%s:%d", itemRegistryName, metadata);
        val displayName = itemStack.getDisplayName();
        val item = Item.builder()
                .registryName(registryName)
                .displayName(displayName)
                .build();
        if (cache.contains(item)) {
            return item;
        }
        cache.add(item);
        RenderState.queueRender(new RenderQuery(RenderType.ITEM, registryName));
        val icon = RenderState.getItemRenderResult();
        insertStmt.setString(1, registryName);
        insertStmt.setString(2, displayName);
        insertStmt.setBytes(3, icon);
        insertStmt.executeUpdate();
        return item;
    }
}
