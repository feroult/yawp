package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.datastore.models.Parent;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Repository;
import org.junit.BeforeClass;

public class DatastoreTestCase {

    protected ConnectionManager connectionManager = new ConnectionManager();

    protected static Repository yawp;

    @BeforeClass
    public static void setUpTestCase() throws Exception {
        createRepository();
    }

    private static void createRepository() {
        yawp = Repository.r().setFeatures(new EndpointScanner(testPackage()).scan());
    }

    @SuppressWarnings("unused")
    private void dropTables() {
        connectionManager.execute("drop schema public cascade; create schema public;");
    }

    protected static String testPackage() {
        return Parent.class.getPackage().getName();
    }

}
