package endpoint.tools;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class DeleteAll {

	public static void now() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query();
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()) {
			datastore.delete(entity.getKey());
		}
	}

}
