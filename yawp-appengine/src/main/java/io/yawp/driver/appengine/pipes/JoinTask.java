package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.repository.query.NoResultException;

import java.util.*;

import static io.yawp.driver.appengine.pipes.CacheHelper.*;
import static io.yawp.repository.Yawp.yawp;

public class JoinTask implements DeferredTask {

    private static final int BUSY_WAIT_TIMES = 20;

    private static final int BUSY_WAIT_SLEEP = 250;

    private String sinkUri;

    private Integer index;

    private transient String lockCacheKey;

    private transient MemcacheService memcache;

    private transient Map<IdRef<SinkMarker>, SinkMarker> sinkMarkerCache;

    private transient Set<IdRef<SinkMarker>> sinkMarkersToSave;

    public JoinTask(String sinkUri, Integer index) {
        this.sinkUri = sinkUri;
        this.index = index;
    }

    @Override
    public void run() {
        init();
        join();
    }

    private void init() {
        this.memcache = MemcacheServiceFactory.getMemcacheService();
        this.lockCacheKey = CacheHelper.createLockCacheKey(sinkUri, index);
        this.sinkMarkerCache = new HashMap<>();
        this.sinkMarkersToSave = new HashSet<>();

    }

    private void join() {
        memcache.increment(createIndexCacheKey(sinkUri), 1);
        memcache.increment(createLockCacheKey(sinkUri, index), -1 * POW_2_15);

        busyWaitForWriters(BUSY_WAIT_TIMES, BUSY_WAIT_SLEEP);
        execute();
    }

    private void execute() {
        List<Work> works = listWorks();

        yawp.begin();
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
            yawp.destroy(work.getId());
        }
    }

    private void commitIfChanged(Object sink, boolean changed) {
        if (!changed) {
            yawp.rollback();
            return;
        }

        for (IdRef<SinkMarker> id : sinkMarkersToSave) {
            yawp.save(sinkMarkerCache.get(id));
        }

        yawp.save(sink);

        // TODO: destroy empty sinks?
        yawp.commit();
    }

    private boolean executeIfLastVersion(Work work, Object sink) {
        SourceMarker sourceVersion = work.getSourceMarker();

        IdRef<SinkMarker> sinkMarkerId = work.createSinkMarkerId();
        SinkMarker sinkMarker = fetchSinkMarker(sinkMarkerId);

        if (sinkMarker.getVersion() >= work.getSourceVersion()) {
            return false;
        }

        work.execute(sink, sinkMarker);

        sinkMarkerCache.put(sinkMarkerId, sinkMarker);
        sinkMarkersToSave.add(sinkMarkerId);

        return true;
    }

    private SinkMarker fetchSinkMarker(IdRef<SinkMarker> sinkMarkerId) {
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
        return yawp(Work.class).where("indexHash", "=", createIndexHash(sinkUri, index)).order("id").list();
    }

    private Object fetchOrCreateSink() {
        IdRef<?> sinkId = IdRef.parse(yawp(), sinkUri);
        try {
            return sinkId.fetch();
        } catch (NoResultException e) {
            try {
                // TODO: Pipes - pipe must provide an api to init sinks
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
