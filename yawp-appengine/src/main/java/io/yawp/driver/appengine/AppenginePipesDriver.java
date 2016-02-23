package io.yawp.driver.appengine;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import io.yawp.driver.api.PipesDriver;
import io.yawp.driver.appengine.pipes.flow.FanoutTask;
import io.yawp.driver.appengine.pipes.flow.Payload;
import io.yawp.driver.appengine.pipes.reflow.ReflowFluxTask;
import io.yawp.driver.appengine.pipes.reflow.ReflowRefluxTask;
import io.yawp.driver.appengine.pipes.reload.ReloadPipeJob;
import io.yawp.driver.appengine.pipes.utils.ClearPipelineTask;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.SourceMarker;
import io.yawp.repository.query.NoResultException;

public class AppenginePipesDriver implements PipesDriver {

    private Repository r;

    public AppenginePipesDriver(Repository r) {
        this.r = r;
    }

    @Override
    public void flux(Pipe pipe, Object source) {
        enqueueFanoutTask(pipe, source, null, true);
    }

    @Override
    public void reflux(Pipe pipe, Object source) {
        enqueueFanoutTask(pipe, source, null, false);
    }

    @Override
    public void refluxOld(Pipe pipe, Object source, Object oldSource) {
        enqueueFanoutTask(pipe, source, oldSource, false);
    }

    @Override
    public void reflow(Pipe pipe, Object sink) {
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(TaskOptions.Builder.withPayload(new ReflowFluxTask(pipe, sink)));
        queue.add(TaskOptions.Builder.withPayload(new ReflowRefluxTask(pipe, sink)));
    }

    @Override
    public void reload(Class<? extends Pipe> pipeClazz) {
        PipelineService service = PipelineServiceFactory.newPipelineService();
        String pipelineId = service.startNewPipeline(new ReloadPipeJob(), pipeClazz);
        ClearPipelineTask.enqueue(pipelineId);
    }

    private void enqueueFanoutTask(Pipe pipe, Object source, Object oldSource, boolean present) {
        SourceMarker sourceMarker = saveSourceMarker(source);
        Queue queue = QueueHelper.getPipeQueue();
        Payload payload = createPayload(pipe, source, sourceMarker, oldSource, present);
        queue.add(TaskOptions.Builder.withPayload(new FanoutTask(payload)));
    }

    private Payload createPayload(Pipe pipe, Object source, SourceMarker marker, Object oldSource, boolean present) {
        Payload payload = new Payload();
        payload.setNs(r.namespace().getNs());
        payload.setPipeClazz(pipe.getClass());
        payload.setSourceJson(source);
        payload.setSourceMarkerJson(marker);
        payload.setOldSourceJson(oldSource);
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

    private SourceMarker saveSourceMarker(Object source) {
        ObjectHolder objectHolder = new ObjectHolder(source);
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
