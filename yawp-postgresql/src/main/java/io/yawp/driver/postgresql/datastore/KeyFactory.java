package io.yawp.driver.postgresql.datastore;

import io.yawp.repository.Namespace;

public class KeyFactory {

    private static Key createKey(Key parent, String kind, String name, Long id) {
        String ns = NamespaceManager.get();
        if (Namespace.GLOBAL.equals(ns)) {
            return new Key(parent, kind, name, id);
        }
        return new Key(ns, parent, kind, name, id);
    }

    public static Key createKey(Key parent, String kind) {
        return createKey(parent, kind, null, null);
    }

    public static Key createKey(String kind) {
        return createKey(null, kind, null, null);
    }

    public static Key createKey(String kind, String name) {
        return createKey(null, kind, name, null);
    }

    public static Key createKey(String kind, Long id) {
        return createKey(null, kind, null, id);
    }

    public static Key createKey(Key parent, String kind, String name) {
        return createKey(parent, kind, name, null);
    }

    public static Key createKey(Key parent, String kind, Long id) {
        return createKey(parent, kind, null, id);
    }

}
