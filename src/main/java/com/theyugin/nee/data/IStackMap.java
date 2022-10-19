package com.theyugin.nee.data;

import java.util.HashMap;

public abstract class IStackMap<T> extends HashMap<Integer, IStack<T>> {
    protected abstract IStack<T> newStack(T v, int amount);

    public final void accumulate(int slot, T item) {
        accumulate(slot, item, 1);
    }

    public final void accumulate(int slot, T item, int amount) {
        if (get(slot) == null) {
            put(slot, newStack(item, amount));
        } else {
            get(slot).contents().add(item);
        }
    }
}
