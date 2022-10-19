package com.theyugin.nee.data;

import java.util.Set;

public class ItemStack implements IStack<Item> {
    private final Set<Item> contents;
    private final int amount;

    public ItemStack(Set<Item> contents, int amount) {
        this.contents = contents;
        this.amount = amount;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public Set<Item> contents() {
        return contents;
    }
}
