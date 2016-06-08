package io.yawp.driver.appengine.pipes.utils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import io.yawp.repository.pipes.Pipe;

public class QueueHelper {
    public static Queue getPipeQueue(Pipe pipe) {
        if (pipe.getQueueName() == null) {
            return getDefaultQueue();
        }
        return QueueFactory.getQueue(pipe.getQueueName());
    }

    public static Queue getDefaultQueue() {
        return QueueFactory.getDefaultQueue();
    }
}
