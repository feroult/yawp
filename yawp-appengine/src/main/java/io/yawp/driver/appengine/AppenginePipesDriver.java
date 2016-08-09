package io.yawp.driver.appengine;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import io.yawp.driver.api.PipesDriver;
import io.yawp.driver.appengine.pipes.flow.FanoutTask;
import io.yawp.driver.appengine.pipes.flow.ForkTask;
import io.yawp.driver.appengine.pipes.flow.Payload;
import io.yawp.driver.appengine.pipes.flow.drops.FlowDropsNamespaceFanoutTask;
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

import java.util.Set;

public class AppenginePipesDriver implements PipesDriver {

    private Repository r;

    public AppenginePipesDriver(Repository r) {
        this.r = r;
    }

    @Override
    public void flux(Pipe pipe, Object source) {
        enqueue(pipe, source, null, true);
    }

    @Override
    public void reflux(Pipe pipe, Object source) {
        enqueue(pipe, source, null, false);
    }

    @Override
    public void refluxOld(Pipe pipe, Object source, Object oldSource) {
        enqueue(pipe, source, oldSource, false);
    }

    @Override
    public void reflow(Pipe pipe, Object sink) {
        Queue queue = QueueHelper.getPipeReflowQueue(pipe);
        queue.add(TaskOptions.Builder.withPayload(new ReflowFluxTask(pipe, sink)));
        queue.add(TaskOptions.Builder.withPayload(new ReflowRefluxTask(pipe, sink)));
    }

    @Override
    public void reload(Class<? extends Pipe> pipeClazz) {
        PipelineService service = PipelineServiceFactory.newPipelineService();
        String pipelineId = service.startNewPipeline(new ReloadPipeJob(), pipeClazz);
        ClearPipelineTask.enqueue(pipelineId);
    }

    @Override
    public void flowDrops() {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new FlowDropsNamespaceFanoutTask()));
    }

    private void enqueue(Pipe pipe, Object source, Object oldSource, boolean present) {
        SourceMarker sourceMarker = saveSourceMarker(source);
        Payload payload = createPayload(pipe, source, sourceMarker, oldSource, present);

        if (!pipe.hasSinks()) {
            fanout(pipe, payload);
        } else {
            fork(pipe, payload);
        }
    }

    private void fork(Pipe pipe, Payload payload) {
        Queue queue = QueueHelper.getPipeForkQueue(pipe);
        Set<IdRef<?>> sinkIds = pipe.allSinks();
        for (IdRef<?> sinkId : sinkIds) {
            payload.setSinkUri(sinkId);
            queue.add(TaskOptions.Builder.withPayload(new ForkTask(payload)));
        }
    }

    private void fanout(Pipe pipe, Payload payload) {
        Queue queue = QueueHelper.getPipeFanoutQueue(pipe);
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
