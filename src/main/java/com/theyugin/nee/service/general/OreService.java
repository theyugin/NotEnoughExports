package com.theyugin.nee.service.general;

import com.theyugin.nee.data.general.Item;
import com.theyugin.nee.data.general.Ore;
import com.theyugin.nee.data.general.OreItem;
import com.theyugin.nee.service.AbstractCacheableService;
import com.theyugin.nee.util.StackUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.item.ItemStack;

public class OreService extends AbstractCacheableService<Ore> {
    private final ItemService itemService;
    private final Set<OreItem> oreItemCache = new HashSet<>();
    private final PreparedStatement insertOreStmt;
    private final PreparedStatement insertOreItemStmt;

    @SneakyThrows
    public OreService(@NonNull Connection conn, ItemService itemService) {
        this.itemService = itemService;
        insertOreStmt = conn.prepareStatement("insert or ignore into ore (name) values (?)");
        insertOreItemStmt = conn.prepareStatement(
                "insert or ignore into ore_item (item_registry_name, item_nbt, ore_name) values (?, ?, ?)");
    }

    @SneakyThrows
    public Ore createOrGet(String name) {
        val ore = new Ore(name);
        if (putInCache(ore)) {
            return ore;
        }
        insertOreStmt.setString(1, name);
        insertOreStmt.executeUpdate();
        return ore;
    }

    @SneakyThrows
    public void addItem(Ore ore, Item item) {
        val oreItem = new OreItem(ore, item);
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
