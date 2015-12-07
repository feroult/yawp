package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.postgresql.Person;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.tools.DatabaseSynchronizer;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Repository;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class DatastoreTestCase {

    protected ConnectionManager connectionManager = new ConnectionManager();

    protected static Repository yawp;

    @BeforeClass
    public static void setUpTestCase() throws Exception {
        configureEnvironment();
        createRepository();
    }

    @AfterClass
    public static void tearDownTestCase() {
        InitialContextSetup.unregister();
    }

    private static void configureEnvironment() {
        Environment.set("test");
        InitialContextSetup.configure("configuration/jetty-env-test.xml");
    }

    private static void createRepository() {
        yawp = Repository.r().setFeatures(new EndpointScanner(testPackage()).scan());
    }

    @SuppressWarnings("unused")
    private void dropTables() {
        connectionManager.execute("drop schema public cascade; create schema public;");
    }

    protected static String testPackage() {
        return Person.class.getPackage().getName();
    }

}
