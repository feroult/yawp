package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.Person;
import io.yawp.driver.postgresql.configuration.InitialContextSetup;
import io.yawp.driver.postgresql.sql.ConnectionPool;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Repository;

import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class DatastoreTestCase {

    protected static Connection connection;

    protected static Repository yawp;

    @BeforeClass
    public static void setUpClass() throws Exception {
        InitialContextSetup.configure();
        createRepository();
        createConnection();
        createTables();
    }

    private static void createRepository() {
        yawp = Repository.r().setFeatures(new EndpointScanner(testPackage()).scan());
    }

    @AfterClass
    public static void tearDownClass() {
        closeConnection();
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
