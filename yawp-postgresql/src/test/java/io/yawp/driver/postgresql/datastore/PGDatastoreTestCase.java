package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.Person;
import io.yawp.driver.postgresql.datastore.sql.SqlRunner;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;

import java.sql.Connection;

import javax.naming.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.postgresql.ds.PGConnectionPoolDataSource;

public class PGDatastoreTestCase {

	protected static Connection connection;

	@BeforeClass
	public static void setUpClass() throws Exception {
		configureInitialContext();
		createConnection();
		createTables();
	}

	@AfterClass
	public static void tearDownClass() {
		closeConnection();
	}

	private static void configureInitialContext() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextMock.class.getName());

		PGConnectionPoolDataSource ds = new PGConnectionPoolDataSource();
		ds.setUrl("jdbc:postgresql://localhost/yawp_test");
		// ds.setUser("MY_USER_NAME");
		// ds.setPassword("MY_USER_PASSWORD");

		InitialContextMock.bind("jdbc/yawp_test", ds);
	}

	private static void createConnection() {
		connection = ConnectionPool.connection();
	}

	private static void closeConnection() {
		ConnectionPool.close(connection);
	}

	private static void createTables() {
		RepositoryFeatures features = new EndpointScanner(testPackage()).scan();
		SchemaSynchronizer.sync(features.getEndpointClazzes());
	}

	@SuppressWarnings("unused")
	private void dropTables() {
		try {
			new SqlRunner(connection, "drop schema public cascade; create schema public;").execute();
		} finally {
			closeConnection();
		}
	}

	protected static String testPackage() {
		return Person.class.getPackage().getName();
	}

}
