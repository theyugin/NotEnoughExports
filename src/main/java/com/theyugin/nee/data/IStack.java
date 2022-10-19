package com.theyugin.nee.data;

import java.util.Set;

public interface IStack<T> {
    int amount();

    Set<T> contents();
}
