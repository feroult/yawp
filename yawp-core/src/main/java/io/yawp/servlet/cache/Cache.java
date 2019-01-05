package io.yawp.servlet.cache;

import io.yawp.repository.IdRef;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Cache {

    private final static Logger logger = Logger.getLogger(Cache.class.getName());

    private static final ThreadLocal<CacheHolder> cache = ThreadLocal.withInitial(CacheHolder::new);

    public static <T> List<T> get(List<IdRef<T>> ids) {
        List<T> objects = new ArrayList<>();
        for (IdRef<T> id : ids) {
            objects.add(get(id));
        }
        return objects;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(IdRef<T> id) {
        T object = (T) cache.get().get(id);
        return object;
    }

    public static <T> void clear(IdRef<T> id) {
        cache.get().clear(id);
    }

    public static void clearAll() {
        cache.get().clear();
    }
}