package com.theyugin.nee.util;

import com.theyugin.nee.NotEnoughExports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class IInputMap<T> extends HashMap<Integer, List<T>> {
    public void accumulate(Integer k, T v) {
        if (get(k) == null)
            this.put(k, new ArrayList<>(Arrays.asList(v)));
        else
            this.get(k).add(v);
    }
}
