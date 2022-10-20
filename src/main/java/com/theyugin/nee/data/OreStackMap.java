package com.theyugin.nee.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OreStackMap extends IStackMap<Ore> {
    @Override
    protected IStack<Ore> newStack(Ore v, int amount, int chance) {
        return new OreStack(Stream.of(v).collect(Collectors.toSet()), amount, chance);
    }
}
