package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions;

import static io.yawp.repository.Yawp.yawp;

public class ForkTask extends PipeBaseTask {

    public ForkTask(Payload payload) {
        super(payload);
    }

    @Override
    public void run() {
        if (!fork()) {
            // TODO: requeue
        }
    }

    private boolean fork() {
        Integer index = getIndexSemaphore();
        String lock = getLockKey(index);

        if (!tryToLock(lock)) {
            return false;
        }

        saveWork(createIndexHash(index));

        try {
            
            enqueue(createForkTask(index));

        } catch (TaskAlreadyExistsException e) {
            // fan-in
        } finally {
            decrementLock(lock);
        }

        return true;
    }

    private void enqueue(TaskOptions taskOptions) {
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(taskOptions);
    }

    private void decrementLock(String lock) {
        memcache.increment(lock, -1);
    }

    private TaskOptions createForkTask(Integer index) {
        long now = System.currentTimeMillis();
        return TaskOptions.Builder.withPayload(new JoinTask(index, payload))
                .taskName(taskName(index, now)).etaMillis(now + 1000);
    }

    private String taskName(Integer index, long now) {
        return String.format("%s-%d-%d", sinkId, now / 1000 / 30, index).replaceAll("/", "__");
    }

    private Integer getIndexSemaphore() {
        String indexKey = INDEX_PREFIX + sinkId;

        Integer index = (Integer) memcache.get(indexKey);

        if (index == null) {
            memcache.put(indexKey, 1);
            index = (Integer) memcache.get(indexKey);
        }

        return index;
    }

    private String getLockKey(Integer index) {
        return String.format("%s-%s-%d", LOCK_PREFIX, sinkId, index);
    }

    private boolean tryToLock(String lock) {
        long writers = memcache.increment(lock, 1, POW_2_16);
        if (writers < POW_2_16) {
            decrementLock(lock);
            return false;
        }
        return true;
    }

    private void saveWork(String indexHash) {
        Work work = new Work(indexHash, payload);
        yawp.save(work);
    }
}
