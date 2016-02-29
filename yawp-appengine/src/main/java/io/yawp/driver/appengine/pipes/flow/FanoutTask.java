package io.yawp.driver.appengine.pipes.flow;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;

import java.util.Set;
import java.util.logging.Logger;

import static io.yawp.repository.Yawp.yawp;

public class FanoutTask implements DeferredTask {

    private final static Logger logger = Logger.getLogger(FanoutTask.class.getName());

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
        log();
        fanout();
    }

    private void init() {
        this.r = yawp().namespace(payload.getNs());
        this.pipe = newPipeInstance();
        this.source = payload.getSource();
    }

    private void log() {
        logger.info(String.format("fanout-task - pipe: %s", payload.getPipeClazz().getName()));
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
        return pipe.allSinks();
    }

    public Set<IdRef<?>> getRemovedSinkIds() {
        Pipe oldPipe = newPipeInstance();
        oldPipe.configureSinks(payload.getOldSource());

        Pipe newPipe = newPipeInstance();
        newPipe.configureSinks(payload.getSource());

        Set oldSinks = oldPipe.allSinks();
        oldSinks.removeAll(newPipe.allSinks());
        return oldSinks;
    }

    private Pipe newPipeInstance() {
        return Pipe.newInstance(r, payload.getPipeClazz());
    }
}
