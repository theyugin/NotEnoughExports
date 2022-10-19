package com.theyugin.nee.sql;

import com.theyugin.nee.data.Item;
import com.theyugin.nee.data.Ore;

public interface ICraftingTableRecipeBuilder<T> extends IDataBuilder<T> {
    ICraftingTableRecipeBuilder<T> addItemInput(Item item, int slot);

    ICraftingTableRecipeBuilder<T> addOreInput(Ore item, int slot);
}
