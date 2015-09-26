package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.Person;
import io.yawp.driver.postgresql.datastore.sql.SqlRunner;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.RepositoryFeatures;

import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class PGDatastoreTestCase {

	protected static Connection connection;

	@BeforeClass
	public static void setUpClass() throws Exception {
		InitialContextMock.configure();
		createConnection();
		createTables();
	}

	@AfterClass
	public static void tearDownClass() {
		closeConnection();
	}

	private static void createConnection() {
		connection = ConnectionPool.connection("ccc");
	}

	private static void closeConnection() {
		ConnectionPool.close(connection, "ccc");
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
