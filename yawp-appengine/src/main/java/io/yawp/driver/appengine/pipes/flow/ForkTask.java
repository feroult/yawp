package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.*;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.Repository;

import java.util.logging.Logger;

import static io.yawp.driver.appengine.pipes.flow.CacheHelper.*;
import static io.yawp.repository.Yawp.yawp;

public class ForkTask implements DeferredTask {

    private final static Logger logger = Logger.getLogger(ForkTask.class.getName());

    private Payload payload;

    private transient Repository r;

    private transient String sinkUri;

    private transient String indexCacheKey;

    private transient MemcacheService memcache;

    public ForkTask(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void run() {
        init();
        log();
        if (!fork()) {
            // requeue
            throw new IllegalStateException();
        }
    }

    private void init() {
        this.r = yawp().namespace(payload.getNs());
        this.sinkUri = payload.getSinkUri();
        this.indexCacheKey = createIndexCacheKey(sinkUri);
        this.memcache = MemcacheServiceFactory.getMemcacheService();
    }

    private void log() {
        logger.info(String.format("fork-task - pipe: %s, sinkId: %s", payload.getPipeClazz().getName(), sinkUri));
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
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(taskOptions);
    }

    private TaskOptions createForkTask(Integer index) {
        long now = System.currentTimeMillis();
        return TaskOptions.Builder.withPayload(new JoinTask(payload.getNs(), sinkUri, index))
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
        r.save(work);
    }
}
