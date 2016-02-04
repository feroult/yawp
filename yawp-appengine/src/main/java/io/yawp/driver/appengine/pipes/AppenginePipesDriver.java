package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.api.PipesDriver;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.VersionMarker;
import io.yawp.repository.query.NoResultException;

import java.util.Set;

public class AppenginePipesDriver implements PipesDriver {

    private Repository r;

    public AppenginePipesDriver(Repository r) {
        this.r = r;
    }

    @Override
    public void flux(Pipe pipe, Object object) {
        VersionMarker marker = saveVersionMarker(object);
        Queue queue = getPipeQueue();
        Set<IdRef<?>> sinks = pipe.getSinks();

        for (IdRef<?> sinkId : sinks) {
            Payload payload = createPayload(pipe, object, sinkId, marker, true);
            queue.add(TaskOptions.Builder.withPayload(new ForkTask(payload)));
        }
    }

    private Queue getPipeQueue() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return QueueFactory.getDefaultQueue();
    }

    private Payload createPayload(Pipe pipe, Object object, IdRef<?> sinkId, VersionMarker marker, boolean present) {
        Payload payload = new Payload();
        payload.setPipeClazz(pipe.getClass());
        payload.setSource(object);
        payload.setSinkId(sinkId);
        payload.setVersionMarkerJson(marker);
        payload.setPresent(present);
        return payload;
    }

    private IdRef<VersionMarker> createVersionMarkerId(ObjectHolder objectHolder) {
        IdRef<?> objectId = objectHolder.getId();
        if (objectId.getId() != null) {
            return objectId.createChildId(VersionMarker.class, objectId.getId());
        }
        return objectId.createChildId(VersionMarker.class, objectId.getName());
    }

    @Override
    public void reflux(Pipe pipe, Object object) {
        Set<IdRef<?>> sinks = pipe.getSinks();

        for (IdRef<?> sinkId : sinks) {
            Object sink = fetchOrCreateSink(sinkId);
            pipe.reflux(object, sink);
            r.driver().persistence().save(sink);
        }
    }

    private VersionMarker saveVersionMarker(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);
        IdRef<VersionMarker> markerId = createVersionMarkerId(objectHolder);

        VersionMarker marker;

        try {
            marker = markerId.fetch();
            marker.increment();
        } catch (NoResultException e) {
            marker = new VersionMarker();
            marker.setId(markerId);
            marker.setParentId(objectHolder.getId());
        }
        r.save(marker);
        return marker;
    }

    private Object fetchOrCreateSink(IdRef<?> id) {
        try {
            return id.fetch();
        } catch (NoResultException e) {
            return createSink(id);
        }
    }

    private Object createSink(IdRef<?> id) {
        try {
            Object sink = id.getClazz().newInstance();
            ObjectHolder holder = new ObjectHolder(sink);
            holder.setId(id);
            return sink;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
