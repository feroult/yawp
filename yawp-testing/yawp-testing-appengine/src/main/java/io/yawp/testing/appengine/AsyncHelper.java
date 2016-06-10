package io.yawp.testing.appengine;

import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncHelper {

    private static CountDownLatch latch = null;

    private AsyncHelper() {}

    public synchronized static void awaitAsync(long timeout, TimeUnit unit) {
        if (latch != null) {
            throw new IllegalStateException("more than one wait is not allowed");
        }

        latch = new CountDownLatch(1);
        try {
            latch.await(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static void release() {
        int countTasks = getCountTasks();
        
        // only me == 1
        if (countTasks == 1) {
            latch.countDown();
            latch = null;
        }
    }

    private static int getCountTasks() {
        Map<String, QueueStateInfo> queueStateInfo = LocalTaskQueueTestConfig.getLocalTaskQueue().getQueueStateInfo();

        int count = 0;

        for (String key : queueStateInfo.keySet()) {
            count += queueStateInfo.get(key).getCountTasks();
        }

        return count;
    }
}
