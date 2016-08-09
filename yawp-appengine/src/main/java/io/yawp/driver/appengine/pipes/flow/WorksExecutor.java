package io.yawp.driver.appengine.pipes.flow;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.query.NoResultException;

import java.util.*;
import java.util.logging.Logger;

import static io.yawp.repository.Yawp.yawp;

public class WorksExecutor {

    private final static Logger logger = Logger.getLogger(WorksExecutor.class.getName());

    private String ns;

    private final List<Work> works;

    private Repository r;

    private Map<IdRef<?>, Object> sinkCache = new HashMap<>();

    private Set<IdRef<?>> sinksToSave = new HashSet<>();

    private Map<IdRef<SinkMarker>, SinkMarker> sinkMarkerCache = new HashMap<>();

    private Set<IdRef<SinkMarker>> sinkMarkersToSave = new HashSet<>();

    public WorksExecutor(Repository r, List<Work> works) {
        this.r = r;
        this.works = works;
        this.r = yawp().namespace(ns);
    }

    public void execute() {
        try {
            r.beginX();
            for (Work work : works) {
                executeIfLastVersion(work);
            }
            commitIfChanged();
        } finally {
            if (r.isTransationInProgress()) {
                r.rollback();
            }
        }
    }

    public void destroy() {
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
}
