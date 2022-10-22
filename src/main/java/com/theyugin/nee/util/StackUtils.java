package com.theyugin.nee.util;

import static com.theyugin.nee.LoadedMods.IC2;

import com.theyugin.nee.data.Fluid;
import com.theyugin.nee.data.Item;
import com.theyugin.nee.render.RenderState;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.sql.FluidDAO;
import com.theyugin.nee.sql.ItemDAO;
import java.sql.SQLException;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class StackUtils {
    public static ItemStack itemStackFromName(String name) {
        String itemName = name.substring(0, name.lastIndexOf(":"));
        int metadata = Integer.parseInt(name.substring(name.lastIndexOf(":") + 1));
        net.minecraft.item.Item item =
                (net.minecraft.item.Item) net.minecraft.item.Item.itemRegistry.getObject(itemName);
        return new ItemStack(item, 1, metadata);
    }

    public static Item createFromStack(ItemDAO itemDAO, ItemStack itemStack) throws SQLException {
        String itemDefName = net.minecraft.item.Item.itemRegistry.getNameForObject(itemStack.getItem());
        int metadata = itemStack.getItemDamage();
        String fullItemName = String.format("%s:%d", itemDefName, metadata);
        byte[] icon;
        if (StackRenderer.isEnabled()) {
            if (RenderState.renderCache.containsKey(fullItemName)) {
                icon = RenderState.renderCache.get(fullItemName);
            } else {
                RenderState.queueRender(fullItemName);
                icon = RenderState.getRenderResult();
            }
        } else {
            icon = null;
        }
        return itemDAO.create(
                fullItemName,
                StackUtils.isWildcard(itemStack)
                        ? new ItemStack(itemStack.getItem(), 1, 0).getDisplayName() + " (Wildcard)"
                        : itemStack.getDisplayName(),
                icon);
    }

    public static Fluid createFromStack(FluidDAO fluidDAO, FluidStack fluidStack) throws SQLException {
        return fluidDAO.create(
                FluidRegistry.getDefaultFluidName(fluidStack.getFluid()),
                fluidStack.getLocalizedName(),
                StackRenderer.renderIcon(fluidStack));
    }

    public static Set<ItemStack> getOreItemStacks(String oreName) {
        return new HashSet<>(OreDictionary.getOres(oreName));
    }

    public static String getOreDictValue(Set<ItemStack> itemStacks) {
        List<Set<String>> oreSets = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            Set<String> oreSet = new HashSet<>();
            for (int oreId : OreDictionary.getOreIDs(itemStack)) {
                oreSet.add(OreDictionary.getOreName(oreId));
            }
            oreSets.add(oreSet);
        }
        if (oreSets.size() > 0) {
            Set<String> reduced = new HashSet<>(oreSets.get(0));
            for (int i = 1; i < oreSets.size(); ++i) {
                reduced.retainAll(oreSets.get(i));
            }
            if (reduced.size() > 0) {
                return new ArrayList<>(reduced).get(0);
            }
        }
        return null;
    }

    public static boolean isItemStack(Object o) {
        return o instanceof ItemStack;
    }

    public static boolean isIC2InputItemStack(Object o) {
        return IC2.isLoaded() && o instanceof ic2.api.recipe.IRecipeInput;
    }

    public static boolean isWildcard(ItemStack itemStack) {
        return itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE;
    }
}
