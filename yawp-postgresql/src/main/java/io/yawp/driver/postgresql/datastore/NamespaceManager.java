package io.yawp.driver.postgresql.datastore;

import io.yawp.repository.Namespace;

public class NamespaceManager {

    private static ThreadLocal<String> namespace = new ThreadLocal<String>();

    private NamespaceManager() {}

    public static String get() {
        String ns = namespace.get();
        return ns == null ? Namespace.GLOBAL : ns;
    }

    public static void set(String ns) {
        namespace.set(ns);
    }

}
