package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.repository.query.NoResultException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.yawp.driver.appengine.pipes.CacheHelper.*;
import static io.yawp.repository.Yawp.yawp;

public class JoinTask implements DeferredTask {

    private String sinkUri;

    private Integer index;

    private transient String lockCacheKey;

    private transient MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    private transient Map<IdRef<SinkMarker>, SinkMarker> sinkMarkerCache = new HashMap<>();

    public JoinTask(String sinkUri, Integer index) {
        this.sinkUri = sinkUri;
        this.index = index;
    }

    @Override
    public void run() {
        join();
    }

    private void join() {
        memcache.increment(createIndexCacheKey(sinkUri), 1);
        memcache.increment(createLockCacheKey(sinkUri, index), -1 * POW_2_15);

        busyWaitForWriters(20, 250);
        processWorks();
    }

    private void processWorks() {
        List<Work> works = listWorks();

        yawp.begin();

        Object sink = fetchSink();

        for (Work work : works) {
            flowIfLastVersion(work);
        }
    }

    private boolean flowIfLastVersion(Work work) {
        SourceMarker sourceVersion = work.getSourceMarker();

        IdRef<SinkMarker> sinkMarkerId = work.createSinkMarkerId();
        SinkMarker sinkMarker = fetchSinkMarker(sinkMarkerId);

        if (sinkMarker.getVersion() >= work.getSourceVersion()) {
            return false;
        }

        return false;
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
