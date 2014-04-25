package endpoint.tools;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Helper {

	private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

	public Entity queryEntity(String kind, String property, Object value) {
		Query q = new Query(kind);
		q.setFilter(new FilterPredicate(property, FilterOperator.EQUAL, value));
		PreparedQuery pq = datastoreService.prepare(q);
		return pq.asSingleEntity();
	}

	public void createEntityWithIndexedProperty(String kind, Object... propertiesAndValues) {
		createEntity(kind, true, propertiesAndValues);
	}

	public void createEntityWithUnindexedProperty(String kind, Object... propertiesAndValues) {
		createEntity(kind, false, propertiesAndValues);
	}

	public void createEntity(String kind, boolean indexed, Object... propertiesAndValues) {
		Entity entity = new Entity(kind);
		for (int i = 0; i < propertiesAndValues.length; i += 2) {
			String property = (String) propertiesAndValues[i];
			Object value = propertiesAndValues[i + 1];
			if (indexed) {
				entity.setProperty(property, value);
			} else {
				entity.setUnindexedProperty(property, value);
			}
		}
		datastoreService.put(entity);
	}
}
