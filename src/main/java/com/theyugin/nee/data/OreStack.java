package com.theyugin.nee.data;

import java.util.Set;

public class OreStack implements IStack<Ore> {
    private final int amount;
    private final Set<Ore> contents;
    private final int chance;

    public OreStack(Set<Ore> contents, int amount, int chance) {
        this.contents = contents;
        this.amount = amount;
        this.chance = chance;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public Set<Ore> contents() {
        return contents;
    }

    @Override
    public int chance() {
        return this.chance;
    }
}
