package com.theyugin.nee.data;

import java.util.Set;

public class FluidStack implements IStack<Fluid> {
    private final int amount;
    private final Set<Fluid> contents;
    private final int chance;

    public FluidStack(Set<Fluid> contents, int amount, int chance) {
        this.contents = contents;
        this.amount = amount;
        this.chance = chance;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public int chance() {
        return chance;
    }

    @Override
    public Set<Fluid> contents() {
        return contents;
    }
}
