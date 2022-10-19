package com.theyugin.nee.data;

public class ShapelessRecipe {
    public final ItemStackMap itemStackMap;
    public final OreStackMap oreStackMap;
    public final Item output;

    public ShapelessRecipe(ItemStackMap itemStackMap, OreStackMap oreStackMap, Item output) {
        this.itemStackMap = itemStackMap;
        this.oreStackMap = oreStackMap;
        this.output = output;
    }
}
