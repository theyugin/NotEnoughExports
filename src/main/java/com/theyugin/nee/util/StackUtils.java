package com.theyugin.nee.util;

import static com.theyugin.nee.LoadedMods.IC2;

import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StackUtils {
    public static Set<ItemStack> getOreItemStacks(String oreName) {
        return new HashSet<>(OreDictionary.getOres(oreName));
    }

    public static String getOreDictValue(ItemStack[] itemStacks) {
        return getOreDictValue(Arrays.asList(itemStacks));
    }

    public static String getOreDictValue(List<ItemStack> itemStacks) {
        return getOreDictValue(new HashSet<>(itemStacks));
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

    public static boolean isNotWildcard(ItemStack itemStack) {
        return !isWildcard(itemStack);
    }

    public static boolean isWildcard(ItemStack itemStack) {
        return itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE;
    }

    public static boolean isGtConfigCircuit(ItemStack i) {
        return i != null
                && i.getUnlocalizedName().equals("gt.integrated_circuit")
                && i.getItemDamage() != 0
                && isNotWildcard(i);
    }

    public static int findGtConfig(ItemStack[] itemStacks) {
        return Arrays.stream(itemStacks)
                .filter(StackUtils::isGtConfigCircuit)
                .findFirst()
                .map(ItemStack::getItemDamage)
                .orElse(-1);
    }
}
