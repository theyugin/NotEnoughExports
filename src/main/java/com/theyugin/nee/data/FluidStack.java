package com.theyugin.nee.data;

import java.util.Set;

public class FluidStack implements IStack<Fluid> {
    private final int amount;
    private final Set<Fluid> contents;

    public FluidStack(Set<Fluid> contents, int amount) {
        this.contents = contents;
        this.amount = amount;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public Set<Fluid> contents() {
        return contents;
    }
}
