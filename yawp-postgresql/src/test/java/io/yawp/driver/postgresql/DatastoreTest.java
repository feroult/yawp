package io.yawp.driver.postgresql;

import static org.junit.Assert.assertEquals;

import javax.naming.Context;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;

public class DatastoreTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextMock.class.getName());

		PGConnectionPoolDataSource ds = new PGConnectionPoolDataSource();
		ds.setUrl("jdbc:postgresql://localhost/yawp");
		// ds.setUser("MY_USER_NAME");
		// ds.setPassword("MY_USER_PASSWORD");

		InitialContextMock.bind("jdbc/yawp_test", ds);
	}

	@Test
	@Ignore
	public void testPutGetEntity() {
		Entity entity = new Entity("person");
		entity.setProperty("name", "jim");

		Datastore datastore = new Datastore();
		datastore.put(entity);

		Entity retrievedEntity = datastore.get(entity.getKey());
		assertEquals("jim", retrievedEntity.getProperty("name"));
	}

}
