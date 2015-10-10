package io.yawp.driver.appengine;

import io.yawp.driver.api.HelpersDriver;

import javax.servlet.Filter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class AppengineHelpersDriver implements HelpersDriver {

	@Override
	public void deleteAll() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query();
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()) {
			datastore.delete(entity.getKey());
		}
	}

	@Override
	public Filter getDevServerFilter() {
		return new AppengineDevServerFilter();
	}

}
