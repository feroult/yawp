package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.Person;
import io.yawp.driver.postgresql.configuration.Configuration;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.sql.ConnectionPool;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Repository;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Connection;

public class DatastoreTestCase {

    protected static Connection connection;

    protected static Repository yawp;

    @BeforeClass
    public static void setUpClass() throws Exception {
        configureEnvironment();
        createRepository();
        createConnection();
        createTables();
    }

    @AfterClass
    public static void tearDownClass() {
        closeConnection();
        InitialContextSetup.unregister();
    }

    private static void configureEnvironment() {
        Configuration.setEnv("test");
        InitialContextSetup.configure("configuration/jetty-env-test.xml");
    }

    private static void createRepository() {
        yawp = Repository.r().setFeatures(new EndpointScanner(testPackage()).scan());
    }

    private static void createConnection() {
        connection = ConnectionPool.connection();
    }

    private static void closeConnection() {
        ConnectionPool.close(connection);
    }

    private static void createTables() {
        SchemaSynchronizer.sync(yawp.getFeatures().getEndpointClazzes());
    }

    @SuppressWarnings("unused")
    private void dropTables() {
        try {
            new SqlRunner("drop schema public cascade; create schema public;").execute(connection);
        } finally {
            closeConnection();
        }
    }

    protected static String testPackage() {
        return Person.class.getPackage().getName();
    }

}
