package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.repository.query.NoResultException;

import java.util.*;

import static io.yawp.driver.appengine.pipes.CacheHelper.*;
import static io.yawp.repository.Yawp.yawp;

public class JoinTask implements DeferredTask {

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

        busyWaitForWriters(20, 250);
        execute();
    }

    private void execute() {
        List<Work> works = listWorks();

        yawp.begin();
        boolean changed = executeWorks(works);
        commitIfChanged(changed);

        destroyWorks(works);
    }

    private boolean executeWorks(List<Work> works) {
        Object sink = fetchSink();

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

    private void commitIfChanged(boolean changed) {
        if (!changed) {
            yawp.rollback();
            return;
        }

        for (IdRef<SinkMarker> id : sinkMarkersToSave) {
            yawp.save(sinkMarkerCache.get(id));
        }

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
        return yawp(Work.class).where("index", "=", createIndexHash(sinkUri, index)).order("id").list();
    }

    private Object fetchSink() {
        return IdRef.parse(yawp(), sinkUri).fetch();
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
