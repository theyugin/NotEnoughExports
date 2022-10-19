package com.theyugin.nee.data;

import java.util.Set;

public class OreStack implements IStack<Ore> {
    private final int amount;
    private final Set<Ore> contents;

    public OreStack(Set<Ore> contents, int amount) {
        this.contents = contents;
        this.amount = amount;
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public Set<Ore> contents() {
        return contents;
    }
}
