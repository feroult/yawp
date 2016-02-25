package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;

import java.util.Set;

import static io.yawp.repository.Yawp.yawp;

public class FanoutTask implements DeferredTask {

    private Payload payload;

    private transient Repository r;

    private transient Pipe pipe;

    private transient Object source;

    public FanoutTask(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void run() {
        init();
        fanout();
    }

    private void init() {
        this.r = yawp().namespace(payload.getNs());
        this.pipe = createPipeInstance();
        this.source = payload.getSource();
    }

    private void fanout() {
        Queue queue = QueueHelper.getPipeQueue();

        for (IdRef<?> sinkId : getSinkIds()) {
            payload.setSinkUri(sinkId);
            queue.add(TaskOptions.Builder.withPayload(new ForkTask(payload)));
        }
    }

    private Set<IdRef<?>> getSinkIds() {
        if (payload.isRefluxOld()) {
            return getRemovedSinkIds();
        }
        return getAddedSinkIds();
    }

    private Set<IdRef<?>> getAddedSinkIds() {
        pipe.configureSinks(source);
        return pipe.getSinks();
    }

    public Set<IdRef<?>> getRemovedSinkIds() {
        Pipe oldPipe = createPipeInstance();
        oldPipe.configureSinks(payload.getOldSource());

        Pipe newPipe = createPipeInstance();
        newPipe.configureSinks(payload.getSource());

        oldPipe.removeSinks(newPipe.getSinks());
        return oldPipe.getSinks();
    }

    private Pipe createPipeInstance() {
        try {
            Pipe pipe = payload.getPipeClazz().newInstance();
            pipe.setRepository(r);
            return pipe;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
