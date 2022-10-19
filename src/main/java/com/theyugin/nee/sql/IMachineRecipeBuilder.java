package com.theyugin.nee.sql;

import com.theyugin.nee.data.Fluid;
import com.theyugin.nee.data.Item;
import com.theyugin.nee.data.Ore;

public interface IMachineRecipeBuilder<T> extends ICraftingTableRecipeBuilder<T> {
    ICraftingTableRecipeBuilder<T> addItemInput(Item item, int slot, int amount);
    ICraftingTableRecipeBuilder<T> addOreInput(Ore item, int slot, int amount);
    ICraftingTableRecipeBuilder<T> addFluidInput(Fluid item, int slot, int amount);

    ICraftingTableRecipeBuilder<T> addItemOutput(Item item, int slot, int amount);
    ICraftingTableRecipeBuilder<T> addFluidOutput(Fluid item, int slot, int amount);
}
