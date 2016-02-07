package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.repository.query.NoResultException;

import java.util.*;

import static io.yawp.driver.appengine.pipes.CacheHelper.POW_2_15;
import static io.yawp.repository.Yawp.yawp;

public class JoinTask implements DeferredTask {

    private static final int BUSY_WAIT_TIMES = 20;

    private static final int BUSY_WAIT_SLEEP = 250;

    private String ns;

    private String sinkUri;

    private Integer index;

    private transient Repository r;

    private transient String indexCacheKey;

    private transient String lockCacheKey;

    private transient String indexHash;

    private transient MemcacheService memcache;

    private transient Map<IdRef<SinkMarker>, SinkMarker> sinkMarkerCache;

    private transient Set<IdRef<SinkMarker>> sinkMarkersToSave;

    public JoinTask(String ns, String sinkUri, Integer index) {
        this.ns = ns;
        this.sinkUri = sinkUri;
        this.index = index;
    }

    @Override
    public void run() {
        init();
        join();
    }

    private void init() {
        this.r = yawp().namespace(ns);
        this.memcache = MemcacheServiceFactory.getMemcacheService();
        this.indexCacheKey = CacheHelper.createIndexCacheKey(sinkUri);
        this.lockCacheKey = CacheHelper.createLockCacheKey(sinkUri, index);
        this.indexHash = CacheHelper.createIndexHash(sinkUri, index);
        this.sinkMarkerCache = new HashMap<>();
        this.sinkMarkersToSave = new HashSet<>();

        System.out.println("join: " + indexHash);
    }

    private void join() {
        memcache.increment(indexCacheKey, 1);
        memcache.increment(lockCacheKey, -1 * POW_2_15);

        busyWaitForWriters(BUSY_WAIT_TIMES, BUSY_WAIT_SLEEP);
        execute();
    }

    private void execute() {
        List<Work> works = listWorks();

        r.begin();
        Object sink = fetchOrCreateSink();
        boolean changed = executeWorks(sink, works);
        commitIfChanged(sink, changed);

        destroyWorks(works);
    }

    private boolean executeWorks(Object sink, List<Work> works) {
        boolean changed = false;

        for (Work work : works) {
            if (executeIfLastVersion(work, sink)) {
                changed = true;
            }
        }
        return changed;
    }

    private void destroyWorks(List<Work> works) {
        for (Work work : works) {
            r.destroy(work.getId());
        }
    }

    private void commitIfChanged(Object sink, boolean changed) {
        if (!changed) {
            r.rollback();
            return;
        }

        for (IdRef<SinkMarker> id : sinkMarkersToSave) {
            r.save(sinkMarkerCache.get(id));
        }

        r.save(sink);

        // TODO: destroy empty sinks?
        r.commit();
    }

    private boolean executeIfLastVersion(Work work, Object sink) {
        SourceMarker sourceVersion = work.getSourceMarker();

        IdRef<SinkMarker> sinkMarkerId = work.createSinkMarkerId();
        SinkMarker sinkMarker = getFromCacheOrFetchSinkMarker(sinkMarkerId);

        if (sinkMarker.getVersion() >= work.getSourceVersion()) {
            return false;
        }

        work.execute(sink, sinkMarker);

        sinkMarkerCache.put(sinkMarkerId, sinkMarker);
        sinkMarkersToSave.add(sinkMarkerId);
        
        return true;
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

    private Object fetchOrCreateSink() {
        IdRef<?> sinkId = IdRef.parse(r, sinkUri);
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

    private void busyWaitForWriters(int times, int sleep) {
        for (int i = 0; i < times; i++) {
            Long counter = (long) memcache.get(lockCacheKey);
            if (counter == null || counter < POW_2_15) {
                break;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
