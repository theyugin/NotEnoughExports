package com.theyugin.nee.data;

import com.theyugin.nee.util.ItemUtils;
import net.minecraft.item.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemBuilder implements IDataBuilder<Item> {
    private final String unlocalizedName;
    private final String localizedName;

    private ItemBuilder(String unlocalizedName, String localizedName) {
        this.localizedName = localizedName;
        this.unlocalizedName = unlocalizedName;
    }

    public static ItemBuilder fromItemStack(ItemStack itemStack) {
        String _unlocalizedName = itemStack.getItem().getUnlocalizedName() + ":" + itemStack.getItemDamage();
        String _localizedName = ItemUtils.isWildcard(itemStack) ? itemStack.getItem().getUnlocalizedName() + " wildcard" : itemStack.getDisplayName();
        return new ItemBuilder(_unlocalizedName, _localizedName);
    }

    public Item save(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert or ignore into item (unlocalizedName, localizedName) values (?, ?)");
        stmt.setString(1, this.unlocalizedName);
        stmt.setString(2, this.localizedName);
        stmt.executeUpdate();
        return new Item(unlocalizedName, localizedName);
    }
}
