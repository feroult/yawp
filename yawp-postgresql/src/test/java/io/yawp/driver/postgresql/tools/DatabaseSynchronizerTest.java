package io.yawp.driver.postgresql.tools;

import io.yawp.driver.postgresql.datastore.DatastoreTestCase;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;
import org.junit.Test;

public class DatabaseSynchronizerTest extends DatastoreTestCase {

    @Test
    public void testCreateDatabaseIfNecessary() {




    }

    @Test
    public void testCreateTables() {
        RepositoryFeatures features = new EndpointScanner(testPackage()).scan();
        DatabaseSynchronizer dbSynchronizer = new DatabaseSynchronizer();
        dbSynchronizer.sync(features.getEndpointClazzes());
    }

}
