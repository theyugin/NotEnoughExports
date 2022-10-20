package com.theyugin.nee.util;

import static com.theyugin.nee.LoadedMods.FORESTRY;
import static com.theyugin.nee.LoadedMods.IC2;

import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtils {

    public static String getUnlocalizedNameSafe(ItemStack itemStack) {
        return itemStack.getItem().getUnlocalizedName() + ":" + itemStack.getItemDamage();
    }

    public static String getLocalizedNameSafe(ItemStack itemStack) {
        return ItemUtils.isWildcard(itemStack)
                ? itemStack.getItem().getUnlocalizedName() + " wildcard"
                : itemStack.getDisplayName();
    }

    public static String getOreDictValue(List<ItemStack> itemStacks) {
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

    public static boolean isComb(ItemStack itemStack) {
        return FORESTRY.isLoaded() && itemStack.getItem() instanceof forestry.apiculture.items.ItemHoneycomb;
    }

    public static boolean isWildcard(ItemStack itemStack) {
        return itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE;
    }
}
