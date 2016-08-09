package io.yawp.driver.appengine.pipes.utils;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import io.yawp.repository.pipes.Pipe;

public class QueueHelper {

    private QueueHelper() {
    }

    public static Queue getDefaultQueue() {
        return QueueFactory.getDefaultQueue();
    }

    private static Queue getPipeDefaultQueue(Pipe pipe) {
        if (pipe.getDefaultQueue() == null) {
            return getDefaultQueue();
        }
        return QueueFactory.getQueue(pipe.getDefaultQueue());
    }

    private static Queue getPipeQueue(Pipe pipe, String queueName) {
        if (queueName == null) {
            return getPipeDefaultQueue(pipe);
        }
        return QueueFactory.getQueue(queueName);
    }

    public static Queue getPipeForkQueue(Pipe pipe) {
        return getPipeQueue(pipe, pipe.getForkQueue());
    }

    public static Queue getPipeJoinQueue(Pipe pipe) {
        return getPipeQueue(pipe, pipe.getJoinQueue());
    }

    public static Queue getPipeReflowQueue(Pipe pipe) {
        return getPipeQueue(pipe, pipe.getReflowQueue());
    }
}
