package io.yawp.driver.postgresql;

import io.yawp.driver.api.NamespaceDriver;
import io.yawp.driver.postgresql.datastore.NamespaceManager;

public class PGNamespaceDriver implements NamespaceDriver {

    @Override
    public String get() {
        return NamespaceManager.get();
    }

    @Override
    public void set(String ns) {
        NamespaceManager.set(ns);
    }

}
