package io.yawp.driver.appengine;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.api.PipesDriver;
import io.yawp.driver.appengine.pipes.ForkTask;
import io.yawp.driver.appengine.pipes.Payload;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.repository.query.NoResultException;

import java.util.Set;

public class AppenginePipesDriver implements PipesDriver {

    private Repository r;

    public AppenginePipesDriver(Repository r) {
        this.r = r;
    }

    @Override
    public void flux(Pipe pipe, Object object) {
        enqueueObjectToPipe(pipe, object, true, new StringBuilder());
    }

    @Override
    public void reflux(Pipe pipe, Object object, StringBuilder sb) {
        enqueueObjectToPipe(pipe, object, false, sb);
    }

    private void enqueueObjectToPipe(Pipe pipe, Object object, boolean present, StringBuilder sb) {
        SourceMarker sourceMarker = saveSourceMarker(object, present, sb);
        Queue queue = getPipeQueue();
        Set<IdRef<?>> sinks = pipe.getSinks();

        for (IdRef<?> sinkId : sinks) {
            Payload payload = createPayload(pipe, object, sinkId, sourceMarker, present);
            queue.add(TaskOptions.Builder.withPayload(new ForkTask(payload)));
        }
    }

    private Queue getPipeQueue() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        return QueueFactory.getDefaultQueue();
    }

    private Payload createPayload(Pipe pipe, Object object, IdRef<?> sinkId, SourceMarker marker, boolean present) {
        Payload payload = new Payload();
        payload.setNs(r.namespace().getNs());
        payload.setPipeClazz(pipe.getClass());
        payload.setSourceJson(object);
        payload.setSinkUri(sinkId);
        payload.setSourceMarkerJson(marker);
        payload.setPresent(present);
        return payload;
    }

    private IdRef<SourceMarker> createSourceMarkerId(ObjectHolder objectHolder) {
        IdRef<?> objectId = objectHolder.getId();
        if (objectId.getId() != null) {
            return objectId.createChildId(SourceMarker.class, objectId.getId());
        }
        return objectId.createChildId(SourceMarker.class, objectId.getName());
    }

    private SourceMarker saveSourceMarker(Object object, boolean present, StringBuilder sb) {
        ObjectHolder objectHolder = new ObjectHolder(object);
        IdRef<SourceMarker> markerId = createSourceMarkerId(objectHolder);

        SourceMarker sourceMarker;

        try {
            sourceMarker = markerId.fetch();
            sourceMarker.increment();
        } catch (NoResultException e) {
            sourceMarker = new SourceMarker();
            sourceMarker.setId(markerId);
            sourceMarker.setParentId(objectHolder.getId());
        }
        r.save(sourceMarker);
        // TODO: remove this (also present)

        if (!present) {
            sb.append(" -- version " + sourceMarker.getVersion() + " -- ");
        }
        //System.out.println(String.format("marker -> %s, %b, %d", markerId, present, sourceMarker.getVersion()));
        return sourceMarker;
    }
}
