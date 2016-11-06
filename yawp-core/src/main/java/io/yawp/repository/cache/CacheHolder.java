package io.yawp.repository.cache;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.NoResultException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CacheHolder<T> {
    private Map<IdRef<T>, T> cache;
    private Set<IdRef<T>> noResults;

    public CacheHolder() {
        clear();
    }

    public void clear() {
        cache = new HashMap<>();
        noResults = new HashSet<>();
    }

    public T get(IdRef<T> id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        if (noResults.contains(id)) {
            throw new NoResultException();
        }
        try {
            T t = id.fetch();
            cache.put(id, t);
            return t;
        } catch (NoResultException e) {
            noResults.add(id);
            throw e;
        }
    }
}