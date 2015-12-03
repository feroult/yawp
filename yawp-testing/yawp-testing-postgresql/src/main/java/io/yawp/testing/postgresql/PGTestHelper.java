package io.yawp.testing.postgresql;

import io.yawp.driver.api.testing.TestHelper;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;
import io.yawp.repository.Repository;

public class PGTestHelper implements TestHelper {

    private Repository r;

    @Override
    public void init(Repository r) {
        this.r = r;
        configureInitialContext();
        resetTables();
    }

    private void configureInitialContext() {
        InitialContextSetup.configure();
    }

    private void resetTables() {
        SchemaSynchronizer.recreate("public");
        SchemaSynchronizer.sync(r.getFeatures().getEndpointClazzes());
    }

    @Override
    public void setUp() {
        SchemaSynchronizer.truncateAll();
    }

    @Override
    public void tearDown() {
    }

}
