package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.query.NoResultException;

import java.util.*;
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

    private transient Map<IdRef<?>, Object> sinkCache;

    private transient Set<IdRef<?>> sinksToSave;

    private transient Map<IdRef<SinkMarker>, SinkMarker> sinkMarkerCache;

    private transient Set<IdRef<SinkMarker>> sinkMarkersToSave;

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
        logger.info(String.format("join-task - sinkGroupId: %s", sinkGroupUri));
    }

    private void join() {
        memcache.increment(indexCacheKey, 1);
        memcache.increment(lockCacheKey, -1 * POW_2_15);

        busyWaitForWriters(BUSY_WAIT_TIMES, BUSY_WAIT_SLEEP);
        execute();
    }

    private void execute() {
        List<Work> works = listWorks();

        try {
            r.beginX();
            for (Work work : works) {
                executeIfLastVersion(work);
            }
            commitIfChanged();
            memcache.delete(lockCacheKey);
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
            r.save(sinkMarkerCache.get(id));
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
        SinkMarker sinkMarker = getFromCacheOrFetchSinkMarker(sinkMarkerId);

        if (sinkMarker.getVersion() >= work.getSourceVersion()) {
            return;
        }

        Object sink = getFromCacheOrFetchOrCreateSink(work.getSinkId());
        work.execute(sink, sinkMarker);

        sinksToSave.add(work.getSinkId());
        sinkMarkerCache.put(sinkMarkerId, sinkMarker);
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

    private SinkMarker getFromCacheOrFetchSinkMarker(IdRef<SinkMarker> sinkMarkerId) {
        if (sinkMarkerCache.containsKey(sinkMarkerId)) {
            return sinkMarkerCache.get(sinkMarkerId);
        }

        SinkMarker sinkMarker = fetchOrCreateSinkMarker(sinkMarkerId);
        sinkMarkerCache.put(sinkMarkerId, sinkMarker);
        return sinkMarker;
    }

    private SinkMarker fetchOrCreateSinkMarker(IdRef<SinkMarker> sinkMarkerId) {
        try {
            return sinkMarkerId.fetch();
        } catch (NoResultException e) {
            SinkMarker sinkMarker = new SinkMarker();
            sinkMarker.setId(sinkMarkerId);
            sinkMarker.setParentId(sinkMarkerId.getParentId());
            sinkMarker.setVersion(0L);
            sinkMarker.setPresent(false);
            return sinkMarker;
        }
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
