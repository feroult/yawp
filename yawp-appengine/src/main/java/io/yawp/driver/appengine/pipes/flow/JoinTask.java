package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.Repository;

import java.util.List;
import java.util.logging.Logger;

import static io.yawp.driver.appengine.pipes.flow.CacheHelper.POW_2_15;
import static io.yawp.repository.Yawp.yawp;

public class JoinTask implements DeferredTask {

    private final static Logger logger = Logger.getLogger(JoinTask.class.getName());

    private static final int BUSY_WAIT_TIMES = 20;

    private static final int BUSY_WAIT_SLEEP = 250;

    private String ns;

    private String sinkGroupUri;

    private Integer index;

    private transient Repository r;

    private transient String indexCacheKey;

    private transient String lockCacheKey;

    private transient String indexHash;

    private transient MemcacheService memcache;

    public JoinTask(String ns, String sinkGroupUri, Integer index) {
        this.ns = ns;
        this.sinkGroupUri = sinkGroupUri;
        this.index = index;
    }

    @Override
    public void run() {
        init();
        log();
        join();
    }

    private void init() {
        this.r = yawp().namespace(ns);
        this.memcache = MemcacheServiceFactory.getMemcacheService(ns);
        this.indexCacheKey = CacheHelper.createIndexCacheKey(sinkGroupUri);
        this.lockCacheKey = CacheHelper.createLockCacheKey(sinkGroupUri, index);
        this.indexHash = CacheHelper.createIndexHash(sinkGroupUri, index);
    }

    private void log() {
        logger.info(String.format("join-task - sinkGroupId: %s", sinkGroupUri));
    }

    private void join() {
        memcache.increment(indexCacheKey, 1);
        memcache.increment(lockCacheKey, -1 * POW_2_15);

        busyWaitForWriters(BUSY_WAIT_TIMES, BUSY_WAIT_SLEEP);
        execute();
    }

    private void execute() {
        WorksExecutor works = new WorksExecutor(r, listWorks());

        works.execute();
        memcache.delete(lockCacheKey);
        works.destroy();
    }


    private List<Work> listWorks() {
        return r.query(Work.class).where("indexHash", "=", indexHash).order("id").list();
    }

    private void busyWaitForWriters(int times, int sleep) {
        for (int i = 0; i < times; i++) {
            Long counter = (Long) memcache.get(lockCacheKey);
            if (counter == null || counter < POW_2_15) {
                break;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
