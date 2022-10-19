package com.theyugin.nee.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemStackMap extends IStackMap<Item> {
    @Override
    protected IStack<Item> newStack(Item v, int amount) {
        return new ItemStack(Stream.of(v).collect(Collectors.toSet()), amount);
    }
}
