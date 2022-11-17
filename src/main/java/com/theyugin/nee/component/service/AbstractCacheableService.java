package com.theyugin.nee.component.service;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCacheableService<T> {
    protected Set<T> cache = new HashSet<>();

    /**
     *
     * @param cacheable thing to check or put in cache
     * @return true if exists; false if new
     */
    protected Boolean putInCache(T cacheable) {
        if (cache.contains(cacheable)) {
            return true;
        }
        cache.add(cacheable);
        return false;
    }
}