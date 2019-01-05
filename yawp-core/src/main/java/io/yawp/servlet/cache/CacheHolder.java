package io.yawp.servlet.cache;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.NoResultException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class CacheHolder {

    private Map<IdRef<?>, Object> cache;

    private Set<IdRef<?>> noResults;

    public CacheHolder() {
        clear();
    }

    public void clear() {
        cache = new HashMap<>();
        noResults = new HashSet<>();
    }

    public <T> void clear(IdRef<T> id) {
        String threadName = Thread.currentThread().getName();
        cache.remove(id);
        noResults.remove(id);
    }


    public Object get(IdRef<?> id) {
        String threadName = Thread.currentThread().getName();
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        if (noResults.contains(id)) {
            throw new NoResultException();
        }
        try {
            Object t = id.refetch();
            cache.put(id, t);
            return t;
        } catch (NoResultException e) {
            noResults.add(id);
            throw e;
        }
    }
}