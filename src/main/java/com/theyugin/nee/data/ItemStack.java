package com.theyugin.nee.data;

import java.util.Set;

public class ItemStack implements IStack<Item> {
    private final Set<Item> contents;
    private final int amount;
    private final int chance;

    public ItemStack(Set<Item> contents, int amount, int chance) {
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
    public Set<Item> contents() {
        return contents;
    }
}
