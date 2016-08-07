package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.AsyncRepository;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.query.NoResultException;

import java.util.*;
import java.util.logging.Logger;

import static io.yawp.driver.appengine.pipes.flow.CacheHelper.POW_2_15;
import static io.yawp.repository.Yawp.yawp;

// semaphore for joins
// http://xion.org.pl/2011/12/10/synchronization-through-memcache/
// http://stackoverflow.com/questions/14907908/google-app-engine-how-to-make-synchronized-actions-using-memcache-or-datastore
// https://cloud.google.com/appengine/articles/best-practices-for-app-engine-memcache


public class JoinTask implements DeferredTask {

    private final static Logger logger = Logger.getLogger(JoinTask.class.getName());

    private static final int BUSY_WAIT_TIMES = 20;

    private static final int BUSY_WAIT_SLEEP = 250;

    private String ns;

    private String sinkGroupUri;

    private Integer index;

    private transient Repository r;

    private AsyncRepository async;

    private transient String indexCacheKey;

    private transient String lockCacheKey;

    private transient String indexHash;

    private transient MemcacheService memcache;

    private transient Map<IdRef<?>, Object> sinkCache;

    private transient Set<IdRef<?>> sinksToSave;

    private transient Map<IdRef<SinkMarker>, FutureObject<SinkMarker>> sinkMarkerCache;

    private transient Set<IdRef<SinkMarker>> sinkMarkersToSave;

    public JoinTask(String ns, String sinkGroupUri, Integer index) {
        this.ns = ns;
        this.sinkGroupUri = sinkGroupUri;
        this.index = index;
    }

    @Override
    public void run() {
//        Mutex mutex = new Mutex("join-task-" + sinkGroupUri, 300l, ns);
//        if (mutex.aquire()) {
//            try {
        init();
        join();
//            } finally {
//                mutex.release();
//            }
//        } else {
//            throw new RuntimeException("cannot aquire mutex for: " + sinkGroupUri);
//        }
    }

    private void init() {
        logger.info(String.format("join-task - sinkGroupId: %s", sinkGroupUri));
        this.r = yawp().namespace(ns);
        this.async = r.async();
        this.memcache = MemcacheServiceFactory.getMemcacheService();
        this.indexCacheKey = CacheHelper.createIndexCacheKey(sinkGroupUri);
        this.lockCacheKey = CacheHelper.createLockCacheKey(sinkGroupUri, index);
        this.indexHash = CacheHelper.createIndexHash(sinkGroupUri, index);
        this.sinkCache = new HashMap<>();
        this.sinksToSave = new HashSet<>();
        this.sinkMarkerCache = new HashMap<>();
        this.sinkMarkersToSave = new HashSet<>();
    }

    private void log() {

    }

    private void join() {
        logger.info("join");
        memcache.increment(indexCacheKey, 1);
        memcache.increment(lockCacheKey, -1 * POW_2_15);

        busyWaitForWriters(BUSY_WAIT_TIMES, BUSY_WAIT_SLEEP);
        execute();
    }

    private void execute() {
        logger.info("execute");

        List<Work> works = listWorks();

        try {
            r.beginX();

            logger.info("cache sink markers");
            for (Work work : works) {
                putSinkMarkerOnCache(work.createSinkMarkerId());
            }

            logger.info("process works");
            for (Work work : works) {
                executeIfLastVersion(work);
            }

            logger.info("save sink and markers");
            commitIfChanged();
            memcache.delete(lockCacheKey);

            logger.info("finish");
        } finally {
            if (r.isTransationInProgress()) {
                r.rollback();
            }
        }

        destroyWorks(works);
    }

    private void destroyWorks(List<Work> works) {
        for (Work work : works) {
            r.destroy(work.getId());
        }
    }

    private void commitIfChanged() {
        if (sinksToSave.isEmpty()) {
            r.rollback();
            return;
        }

        for (IdRef<SinkMarker> id : sinkMarkersToSave) {
            async.save(getSinkMarkerFromCache(id));
        }

        for (IdRef<?> sinkId : sinksToSave) {
            logSave(sinkId);
            r.saveWithHooks(sinkCache.get(sinkId));
        }

        // TODO: destroy empty sinks?
        r.commit();
    }

    private void logSave(IdRef<?> sinkId) {
        logger.info(String.format("join-task - saving sinkId: %s", sinkId.getUri()));
    }

    private void executeIfLastVersion(Work work) {
        IdRef<SinkMarker> sinkMarkerId = work.createSinkMarkerId();
        SinkMarker sinkMarker = getSinkMarkerFromCache(sinkMarkerId);

        if (sinkMarker.getVersion() >= work.getSourceVersion()) {
            return;
        }

        Object sink = getFromCacheOrFetchOrCreateSink(work.getSinkId());
        work.execute(sink, sinkMarker);

        sinksToSave.add(work.getSinkId());
//        sinkMarkerCache.put(sinkMarkerId, sinkMarker);
        sinkMarkersToSave.add(sinkMarkerId);
    }

    private Object getFromCacheOrFetchOrCreateSink(IdRef<?> sinkId) {
        if (sinkCache.containsKey(sinkId)) {
            return sinkCache.get(sinkId);
        }

        Object sink = fetchOrCreateSink(sinkId);
        sinkCache.put(sinkId, sink);
        return sink;
    }

    private Object fetchOrCreateSink(IdRef<?> sinkId) {
        try {
            return sinkId.fetch();
        } catch (NoResultException e) {
            try {
                Object sink = sinkId.getClazz().newInstance();
                ObjectHolder objectHolder = new ObjectHolder(sink);
                objectHolder.setId(sinkId);
                if (sinkId.getParentClazz() != null) {
                    objectHolder.setParentId(sinkId.getParentId());
                }
                return sink;
            } catch (InstantiationException | IllegalAccessException e1) {
                throw new RuntimeException(e);
            }
        }
    }

    private SinkMarker getSinkMarkerFromCache(IdRef<SinkMarker> sinkMarkerId) {
        SinkMarker sinkMarker = sinkMarkerCache.get(sinkMarkerId).get();
        if (sinkMarker == null) {
            sinkMarker = new SinkMarker();
            sinkMarker.setId(sinkMarkerId);
            sinkMarker.setParentId(sinkMarkerId.getParentId());
            sinkMarker.setVersion(0L);
            sinkMarker.setPresent(false);
            sinkMarkerCache.put(sinkMarkerId, new FutureObject<SinkMarker>(sinkMarker));
        }
        return sinkMarker;
    }

    private void putSinkMarkerOnCache(IdRef<SinkMarker> sinkMarkerId) {
        FutureObject<SinkMarker> futureSinkMarker = fetchOrCreateSinkMarker(sinkMarkerId);
        sinkMarkerCache.put(sinkMarkerId, futureSinkMarker);
    }

    private FutureObject<SinkMarker> fetchOrCreateSinkMarker(IdRef<SinkMarker> sinkMarkerId) {
        return async.fetch(sinkMarkerId);
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
