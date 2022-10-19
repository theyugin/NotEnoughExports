package com.theyugin.nee.data;

public class ShapedRecipe {
    public final ItemStackMap itemStackMap;
    public final OreStackMap oreStackMap;
    public final Item output;

    public ShapedRecipe(ItemStackMap itemStackMap, OreStackMap oreStackMap, Item output) {
        this.itemStackMap = itemStackMap;
        this.oreStackMap = oreStackMap;
        this.output = output;
    }
}
