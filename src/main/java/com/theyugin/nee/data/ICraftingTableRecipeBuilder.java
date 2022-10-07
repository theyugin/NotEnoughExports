package com.theyugin.nee.data;

public interface ICraftingTableRecipeBuilder<T> extends IDataBuilder<T> {
    ICraftingTableRecipeBuilder<T> addItemInput(Item item, int slot);
    ICraftingTableRecipeBuilder<T> addOreInput(Ore item, int slot);
}
