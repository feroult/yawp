package io.yawp.driver.postgresql.datastore;

import javax.naming.Context;

import org.junit.BeforeClass;
import org.postgresql.ds.PGConnectionPoolDataSource;

public class PGDatastoreTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextMock.class.getName());

		PGConnectionPoolDataSource ds = new PGConnectionPoolDataSource();
		ds.setUrl("jdbc:postgresql://localhost/yawp_test");
		// ds.setUser("MY_USER_NAME");
		// ds.setPassword("MY_USER_PASSWORD");

		InitialContextMock.bind("jdbc/yawp_test", ds);
	}
}
