package io.yawp.driver.appengine.pipes.utils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import io.yawp.repository.pipes.Pipe;

public class QueueHelper {

    private QueueHelper() {}

    public static Queue getPipeQueue(Pipe pipe) {
        if (pipe.getQueueName() == null) {
            return getDefaultQueue();
        }

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return QueueFactory.getQueue(pipe.getQueueName());
    }

    public static Queue getDefaultQueue() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return QueueFactory.getDefaultQueue();
    }
}
