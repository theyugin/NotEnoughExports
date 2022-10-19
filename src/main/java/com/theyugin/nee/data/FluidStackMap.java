package com.theyugin.nee.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FluidStackMap extends IStackMap<Fluid> {
    @Override
    protected IStack<Fluid> newStack(Fluid v, int amount) {
        return new FluidStack(Stream.of(v).collect(Collectors.toSet()), amount);
    }
}
