package io.yawp.repository.cache;

import io.yawp.repository.IdRef;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static class C<T> extends ThreadLocal<CacheHolder<T>> {
        @Override
        protected CacheHolder<T> initialValue() {
            return new CacheHolder<>();
        }
    }

    private static final Map<Class<?>, C<?>> ms = new HashMap<>();

    public static <T> T get(IdRef<T> t) {
        if (!ms.containsKey(t.getClazz())) {
            ms.put(t.getClazz(), new C<>());
        }
        C<T> c = (C<T>) ms.get(t.getClazz());
        return c.get().get(t);
    }

    public static void clearAll() {
        for (C<?> l : ms.values()) {
            l.get().clear();
        }
    }

}