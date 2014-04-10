package endpoint.tools;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class ReIndex {

	public static void now(String kind, String... properties) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(kind);
		PreparedQuery pq = datastoreService.prepare(q);

		Iterable<Entity> result = pq.asIterable();
		for (Entity entity : result) {
			for (int i = 0; i < properties.length; i++) {
				String property = properties[i];
				entity.setProperty(property, entity.getProperty(property));
			}
			datastoreService.put(entity);
		}
	}

}
