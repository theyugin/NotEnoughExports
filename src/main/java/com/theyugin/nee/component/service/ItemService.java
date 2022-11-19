package com.theyugin.nee.component.service;

import codechicken.nei.util.NBTJson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.Config;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.util.StackUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.item.ItemStack;

@Singleton
public class ItemService extends AbstractCacheableService<Item> {
    private final PreparedStatement insertStmt;

    @Inject
    @SneakyThrows
    public ItemService(@NonNull Connection conn) {
        insertStmt = conn.prepareStatement(
                "insert or ignore into item (registry_name, display_name, nbt, icon) values (?, ?, ?, ?)");
    }

    @SneakyThrows
    public Item processItemStack(@NonNull ItemStack itemStack) {
        val itemRegistryName = net.minecraft.item.Item.itemRegistry.getNameForObject(itemStack.getItem());
        int metadata = itemStack.getItemDamage();
        String registryName;
        String displayName;
        val nbt = itemStack.hasTagCompound() ? NBTJson.toJson(itemStack.stackTagCompound) : "{}";
        if (StackUtils.isNotWildcard(itemStack)) {
            registryName = String.format("%s:%d", itemRegistryName, metadata);
            displayName = itemStack.getDisplayName();
        } else {
            registryName = String.format("%s:*", itemRegistryName);
            displayName = new ItemStack(itemStack.getItem(), 1, 0).getDisplayName() + " (wildcard)";
        }
        val item = Item.builder()
                .registryName(registryName)
                .displayName(displayName)
                .nbt(nbt)
                .build();
        if (putInCache(item)) {
            return item;
        }
        byte[] icon;
        if (Config.exportIcons()) {
            val renderIS = itemStack.copy();
            renderIS.stackSize = 1;
            if (StackUtils.isWildcard(renderIS)) {
                renderIS.itemDamage = 0;
            }
            icon = StackRenderer.renderIcon(renderIS);
        } else {
            icon = null;
        }
        insertStmt.setString(1, registryName);
        insertStmt.setString(2, displayName);
        insertStmt.setString(3, nbt);
        insertStmt.setBytes(4, icon);
        insertStmt.executeUpdate();
        return item;
    }
}
