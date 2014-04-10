package endpoint.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import endpoint.utils.EndpointTestCase;

public class ReIndexTest extends EndpointTestCase {

	private DatastoreService datastoreService;

	@Before
	public void before() {
		datastoreService = DatastoreServiceFactory.getDatastoreService();
	}

	@Test
	public void testReIndexOneProperty() {
		createEntityWithProperty("person", "name", "john");
		createEntityWithProperty("person", "name", "peter");

		assertNull(queryEntity("person", "name", "john"));
		assertNull(queryEntity("person", "name", "peter"));

		ReIndex.now("person", "name");

		assertNotNull(queryEntity("person", "name", "john"));
		assertNotNull(queryEntity("person", "name", "peter"));
	}

	@Test
	public void testReIndexTwoProperties() {
		createEntityWithProperty("person", "name", "john", "age", 10);

		assertNull(queryEntity("person", "name", "john"));
		assertNull(queryEntity("person", "age", 10));

		ReIndex.now("person", "name", "age");

		assertNotNull(queryEntity("person", "name", "john"));
		assertNotNull(queryEntity("person", "age", 10));
	}

	private Entity queryEntity(String kind, String property, Object value) {
		Query q = new Query(kind);
		q.setFilter(new FilterPredicate(property, FilterOperator.EQUAL, value));
		PreparedQuery pq = datastoreService.prepare(q);
		return pq.asSingleEntity();
	}

	private void createEntityWithProperty(String kind, Object... propertiesAndValues) {
		Entity entity = new Entity(kind);
		for (int i = 0; i < propertiesAndValues.length; i += 2) {
			String property = (String) propertiesAndValues[i];
			Object value = propertiesAndValues[i + 1];
			entity.setUnindexedProperty(property, value);
		}
		datastoreService.put(entity);
	}

}
