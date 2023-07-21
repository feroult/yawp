package io.yawp.testing.appengine;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AsyncHelper {

    private AsyncHelper() {
    }

    public synchronized static void awaitAsync(long timeout, TimeUnit unit) {
        while (! io.yawp.driver.appengine.pipes.flow.drops.FlowDropsTask.getHasExecuted()) {
            Thread.yield();
        }
        long limit = System.currentTimeMillis() + unit.toMillis(timeout);

        while (true) {
            if (System.currentTimeMillis() > limit) {
                throw new RuntimeException("await timout");
            }
            if (getCountTasks() == 0) {
                break;
            }
            Thread.yield();
        }
    }

    private static int getCountTasks() {
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();

        Map<String, QueueStateInfo> queueStateInfo = localTaskQueue.getQueueStateInfo();

        int count = 0;

        for (String key : queueStateInfo.keySet()) {
            count += queueStateInfo.get(key).getCountTasks();
        }

        return count;
    }
}
