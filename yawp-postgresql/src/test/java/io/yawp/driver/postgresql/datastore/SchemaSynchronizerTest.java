package io.yawp.driver.postgresql.datastore;

import io.yawp.repository.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;

import org.junit.Test;

public class SchemaSynchronizerTest extends DatastoreTestCase {

    @Test
    public void testCreateTables() {
        RepositoryFeatures features = new EndpointScanner(testPackage()).scan();
        SchemaSynchronizer schemaSynchronizer = new SchemaSynchronizer();
        schemaSynchronizer.sync(features.getEndpointClazzes());
    }

}
