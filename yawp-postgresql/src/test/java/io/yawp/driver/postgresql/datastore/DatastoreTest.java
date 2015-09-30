package io.yawp.driver.postgresql.datastore;

import static io.yawp.repository.query.condition.Condition.and;
import static io.yawp.repository.query.condition.Condition.c;
import static org.junit.Assert.assertEquals;
import io.yawp.driver.postgresql.Person;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatastoreTest extends DatastoreTestCase {

	private Datastore datastore;

	@Before
	public void before() {
		datastore = Datastore.create(new ConnectionManager());
		truncate();
	}

	@After
	public void after() {
		// truncate();
	}

	private void truncate() {
		new SqlRunner("truncate table people;").execute(connection);
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

	@Test
	public void testSimpleQuery() throws FalsePredicateException {
		savePersonWithName("jim");
		savePersonWithName("robert");

		QueryBuilder<Person> builder = QueryBuilder.q(Person.class, yawp);
		builder.where(c("name", "=", "jim"));

		List<Entity> entities = datastore.query(new Query(builder, false));

		assertEquals(1, entities.size());
		assertEquals("jim", entities.get(0).getProperty("name"));
	}

	@Test
	public void testJoinedQuery() throws FalsePredicateException {
		savePersonWithName("jim");
		savePersonWithName("robert");

		QueryBuilder<Person> builder = QueryBuilder.q(Person.class, yawp);
		builder.where(and(c("name", "=", "jim"), c("name", ">", "j")));

		List<Entity> entities = datastore.query(new Query(builder, false));

		assertEquals(1, entities.size());
		assertEquals("jim", entities.get(0).getProperty("name"));
	}

	@Test
	public void testQueryOrder() throws FalsePredicateException {
		savePersonWithName("jim");
		savePersonWithName("robert");

		QueryBuilder<Person> builder = QueryBuilder.q(Person.class, yawp);
		builder.order("name", "desc");

		List<Entity> entities = datastore.query(new Query(builder, false));

		assertEquals(2, entities.size());
		assertEquals("robert", entities.get(0).getProperty("name"));
		assertEquals("jim", entities.get(1).getProperty("name"));
	}

	private void savePersonWithName(String name) {
		Entity entity = new Entity("people");
		entity.setProperty("name", name);
		entity.setProperty("__name", name);
		datastore.put(entity);
	}
}
