package io.yawp.driver.appengine.pipes.helpers;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

public class QueueHelper {
    public static Queue getPipeQueue() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return QueueFactory.getDefaultQueue();
    }

}
