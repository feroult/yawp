package io.yawp.driver.appengine;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.*;
import io.yawp.driver.api.HelpersDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppengineHelpersDriver implements HelpersDriver {

	private final static Logger logger = Logger.getLogger(AppengineHelpersDriver.class.getName());

	@Override
	public void deleteAll() {
		for (String ns : listNamespaces()) {
			deleteAll(ns);
		}
	}

	@Override
	public void deleteAll(String namespace) {
		NamespaceManager.set(namespace);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query();
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()) {
			String kind = entity.getKind();
			if (kind.startsWith("__") && !kind.contains("yawp")) {
				continue;
			}
			datastore.delete(entity.getKey());
		}
	}

	@Override
	public List<String> listNamespaces() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("__namespace__").setKeysOnly();
		PreparedQuery pq = ds.prepare(query);
		List<String> namespaces = new ArrayList<>();
		for (Entity entity : pq.asIterable()) {
			namespaces.add(entity.getKey().getName());
		}
		return namespaces;
	}

	@Override
	public void sync() {
		logger.log(Level.INFO, "appengine helper");
	}

}
