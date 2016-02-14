package io.yawp.driver.appengine;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import io.yawp.driver.api.PipesDriver;
import io.yawp.driver.appengine.pipes.ForkTask;
import io.yawp.driver.appengine.pipes.Payload;
import io.yawp.driver.appengine.pipes.helpers.QueueHelper;
import io.yawp.driver.appengine.pipes.tools.ClearPipelineTask;
import io.yawp.driver.appengine.pipes.tools.reload.ReloadPipeJob;
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
        enqueueObjectToPipe(pipe, object, true);
    }

    @Override
    public void reflux(Pipe pipe, Object object) {
        enqueueObjectToPipe(pipe, object, false);
    }

    @Override
    public void reload(Class<? extends Pipe> pipeClazz) {
        PipelineService service = PipelineServiceFactory.newPipelineService();
        String pipelineId = service.startNewPipeline(new ReloadPipeJob(), pipeClazz);
        ClearPipelineTask.enqueue(pipelineId);
    }

    private void enqueueObjectToPipe(Pipe pipe, Object object, boolean present) {
        SourceMarker sourceMarker = saveSourceMarker(object);
        Queue queue = QueueHelper.getPipeQueue();
        Set<IdRef<?>> sinks = pipe.getSinks();

        for (IdRef<?> sinkId : sinks) {
            Payload payload = createPayload(pipe, object, sinkId, sourceMarker, present);
            queue.add(TaskOptions.Builder.withPayload(new ForkTask(payload)));
        }
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

    private SourceMarker saveSourceMarker(Object object) {
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
        return sourceMarker;
    }
}
