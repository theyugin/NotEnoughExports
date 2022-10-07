package com.theyugin.nee.data;

import com.theyugin.nee.util.ItemInputMap;
import com.theyugin.nee.util.OreInputMap;

public class ShapelessRecipe {
    public final ItemInputMap itemInputMap;
    public final OreInputMap oreInputMap;
    public final Item output;

    public ShapelessRecipe(ItemInputMap itemInputMap, OreInputMap oreInputMap, Item output) {
        this.itemInputMap = itemInputMap;
        this.oreInputMap = oreInputMap;
        this.output = output;
    }
}
