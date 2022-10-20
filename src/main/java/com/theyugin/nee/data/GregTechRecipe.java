package com.theyugin.nee.data;

public class GregTechRecipe {
    public final String recipeType;
    public final ItemStackMap inputItemStackMap;
    public final FluidStackMap inputFluidStackMap;
    public final ItemStackMap outputItemStackMap;
    public final FluidStackMap outputFluidStackMap;
    public final int duration;
    public final int amperage;
    public final int voltage;
    public final int config;

    public GregTechRecipe(
            String recipeType,
            ItemStackMap inputItemStackMap,
            FluidStackMap inputFluidStackMap,
            ItemStackMap outputItemStackMap,
            FluidStackMap outputFluidStackMap,
            int duration,
            int amperage,
            int voltage,
            int config) {
        this.recipeType = recipeType;
        this.inputItemStackMap = inputItemStackMap;
        this.inputFluidStackMap = inputFluidStackMap;
        this.outputItemStackMap = outputItemStackMap;
        this.outputFluidStackMap = outputFluidStackMap;
        this.duration = duration;
        this.amperage = amperage;
        this.voltage = voltage;
        this.config = config;
    }
}
