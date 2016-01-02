package io.yawp.driver.postgresql.tools;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.datastore.DatastoreTestCase;
import io.yawp.repository.scanner.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseSynchronizerTest extends DatastoreTestCase {

    @Before
    public void setUp() {
        Environment.set("test_creation");
        InitialContextSetup.configure("configuration/jetty-env-test.xml");
    }

    @Test
    public void testDatabaseCreation() {
        DatabaseSynchronizer dbSynchronizer = new DatabaseSynchronizer();

        dbSynchronizer.drop();
        assertFalse(dbSynchronizer.databaseExists());

        dbSynchronizer.create();
        assertTrue(dbSynchronizer.databaseExists());

        dbSynchronizer.drop();
    }

    @Test
    public void testCreateTables() {
        RepositoryFeatures features = new EndpointScanner(testPackage()).scan();
        DatabaseSynchronizer dbSynchronizer = new DatabaseSynchronizer();
        dbSynchronizer.sync(features.getEndpointClazzes());
    }

}
