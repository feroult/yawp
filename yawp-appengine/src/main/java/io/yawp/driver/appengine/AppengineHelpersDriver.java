package io.yawp.driver.appengine;

import io.yawp.driver.api.HelpersDriver;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppengineHelpersDriver implements HelpersDriver {

    private final static Logger logger = Logger.getLogger(AppengineHelpersDriver.class.getName());

    @Override
    public void deleteAll() {
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
    public void sync() {
        logger.log(Level.INFO, "appengine helper");
    }

}
