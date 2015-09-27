package io.yawp.driver.postgresql.datastore;

import static org.junit.Assert.assertEquals;
import io.yawp.driver.postgresql.connection.ConnectionManager;
import io.yawp.driver.postgresql.connection.SqlRunner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PGDatastoreTest extends PGDatastoreTestCase {

	private PGDatastore datastore;

	@Before
	public void before() {
		datastore = PGDatastore.create(new ConnectionManager());
		truncate();
	}

	private void truncate() {
		new SqlRunner("truncate table people;").execute(connection);
	}

	@Test
	@Ignore
	public void testPopulate() {
		for (int i = 0; i < 1000; i++) {
			Entity entity = new Entity("people");
			entity.setProperty("name", NameGenerator.generate());
			datastore.put(entity);
		}
	}

	@Test
	public void testCreateRetrieveEntity() throws EntityNotFoundException {
		Entity entity = new Entity("people");
		entity.setProperty("name", "jim");

		datastore.put(entity);

		Entity retrievedEntity = datastore.get(entity.getKey());
		assertEquals("jim", retrievedEntity.getProperty("name"));
	}

	@Test
	public void testCreateUpdateEntity() throws EntityNotFoundException {
		Entity entity = new Entity("people");
		entity.setProperty("name", "jim");

		Key key = datastore.put(entity);

		entity.setProperty("name", "robert");
		datastore.put(entity);

		Entity retrievedEntity = datastore.get(key);
		assertEquals("robert", retrievedEntity.getProperty("name"));

	}

	@Test(expected = EntityNotFoundException.class)
	public void delete() throws EntityNotFoundException {
		Key key = KeyFactory.createKey("people", "xpto");
		Entity entity = new Entity(key);
		datastore.put(entity);

		datastore.delete(key);

		datastore.get(key);
	}

	@Test
	public void testForceName() throws EntityNotFoundException {
		Key key = KeyFactory.createKey("people", "xpto");

		Entity entity = new Entity(key);
		entity.setProperty("name", "jim");

		datastore.put(entity);

		Entity retrievedEntity = datastore.get(key);
		assertEquals("jim", retrievedEntity.getProperty("name"));
	}

	@Test
	public void testForceId() throws EntityNotFoundException {
		Key key = KeyFactory.createKey("people", 123l);

		Entity entity = new Entity(key);
		entity.setProperty("name", "jim");

		datastore.put(entity);

		Entity retrievedEntity = datastore.get(key);
		assertEquals("jim", retrievedEntity.getProperty("name"));
	}

	@Test(expected = EntityNotFoundException.class)
	public void testChildKey() throws EntityNotFoundException {
		Key parentKey = KeyFactory.createKey("parents", 1l);
		Key childKey = KeyFactory.createKey(parentKey, "people", 1l);

		Entity entity = new Entity(childKey);
		entity.setProperty("name", "jim");

		datastore.put(entity);

		Entity retrievedEntity = datastore.get(childKey);
		assertEquals("jim", retrievedEntity.getProperty("name"));

		Key anotherParentKey = KeyFactory.createKey("parents", 2l);
		Key anotherChildKey = KeyFactory.createKey(anotherParentKey, "people", 1l);

		datastore.get(anotherChildKey);
	}

	@Test(expected = EntityNotFoundException.class)
	public void testGrandchildKey() throws EntityNotFoundException {
		Key parentKey = KeyFactory.createKey("parents", 1l);
		Key childKey = KeyFactory.createKey(parentKey, "children", 1l);
		Key grandchildKey = KeyFactory.createKey(childKey, "people", 1l);

		Entity entity = new Entity(grandchildKey);
		entity.setProperty("name", "jim");

		datastore.put(entity);

		Entity retrievedEntity = datastore.get(grandchildKey);
		assertEquals("jim", retrievedEntity.getProperty("name"));

		Key anotherParentKey = KeyFactory.createKey("parents", 2l);
		Key anotherChildKey = KeyFactory.createKey(anotherParentKey, "children", 1l);
		Key anotherGrandchildKey = KeyFactory.createKey(anotherChildKey, "people", 1l);

		datastore.get(anotherGrandchildKey);
	}

}
