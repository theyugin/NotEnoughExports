package com.theyugin.nee.data;

import java.util.Set;

public interface IStack<T> {
    int amount();

    int chance();

    Set<T> contents();
}
