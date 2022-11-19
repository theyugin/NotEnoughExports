package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Item;
import com.theyugin.nee.persistence.general.Ore;
import com.theyugin.nee.persistence.general.OreItem;
import com.theyugin.nee.util.StackUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.item.ItemStack;

@Singleton
public class OreService extends AbstractCacheableService<Ore> {
    private final ItemService itemService;
    private final Set<OreItem> oreItemCache = new HashSet<>();
    private final PreparedStatement insertOreStmt;
    private final PreparedStatement insertOreItemStmt;

    @Inject
    @SneakyThrows
    public OreService(@NonNull Connection conn, ItemService itemService) {
        this.itemService = itemService;
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

    @SneakyThrows
    @Nullable
    public Ore process(String oreName) {
        if (oreName == null) {
            return null;
        }
        val ore = createOrGet(oreName);
        for (val stack : StackUtils.getOreItemStacks(oreName)) {
            addItem(ore, itemService.processItemStack(stack));
        }
        ;
        return ore;
    }

    @Nullable
    public Ore process(List<ItemStack> itemStacks) {
        return process(StackUtils.getOreDictValue(itemStacks));
    }

    @Nullable
    public Ore process(ItemStack[] itemStacks) {
        return process(StackUtils.getOreDictValue(itemStacks));
    }

    @Nullable
    public Ore process(Set<ItemStack> itemStacks) {
        return process(StackUtils.getOreDictValue(itemStacks));
    }
}
