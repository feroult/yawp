package io.yawp.driver.postgresql;

import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;

public class PGHelpersDriver implements HelpersDriver {

    @Override
    public void deleteAll() {
        SchemaSynchronizer.truncateAll();
    }

    @Override
    public void sync() {
        System.out.println("postgresql helper");
    }

}
