package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.*;

import static io.yawp.driver.appengine.pipes.CacheHelper.*;
import static io.yawp.repository.Yawp.yawp;

public class ForkTask implements DeferredTask {

    private Payload payload;

    private transient String sinkUri;

    private transient String indexCacheKey;

    private transient MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    public ForkTask(Payload payload) {
        this.payload = payload;
        this.sinkUri = payload.getSinkUri();
        this.indexCacheKey = createIndexCacheKey(sinkUri);
    }

    @Override
    public void run() {
        if (!fork()) {
            // TODO: requeue
        }
    }

    private boolean fork() {
        Integer index = getIndexSemaphore();
        String lockCacheKey = createLockCacheKey(sinkUri, index);

        if (!tryToLock(lockCacheKey)) {
            return false;
        }

        saveWork(createIndexHash(sinkUri, index));

        try {

            enqueue(createForkTask(index));

        } catch (TaskAlreadyExistsException e) {
            // fan-in
        } finally {
            memcache.increment(lockCacheKey, -1);
        }

        return true;
    }

    private void enqueue(TaskOptions taskOptions) {
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(taskOptions);
    }

    private TaskOptions createForkTask(Integer index) {
        long now = System.currentTimeMillis();
        return TaskOptions.Builder.withPayload(new JoinTask(sinkUri, index))
                .taskName(taskName(index, now)).etaMillis(now + 1000);
    }

    private String taskName(Integer index, long now) {
        return String.format("%s-%d-%d", sinkUri, now / 1000 / 30, index).replaceAll("/", "__");
    }

    private Integer getIndexSemaphore() {
        Integer index = (Integer) memcache.get(indexCacheKey);

        if (index == null) {
            memcache.put(indexCacheKey, 1);
            index = (Integer) memcache.get(indexCacheKey);
        }

        return index;
    }

    private boolean tryToLock(String lock) {
        long writers = memcache.increment(lock, 1, POW_2_16);
        if (writers < POW_2_16) {
            memcache.increment(lock, -1);
            return false;
        }
        return true;
    }

    private void saveWork(String indexHash) {
        Work work = new Work(indexHash, payload);
        yawp.save(work);
    }
}
