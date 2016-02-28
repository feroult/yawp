package io.yawp.driver.appengine.pipes.reflow;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.pump.ObjectPump;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static io.yawp.repository.Yawp.yawp;

public class ReflowFluxTask implements DeferredTask {

    private final static Logger logger = Logger.getLogger(ReflowFluxTask.class.getName());

    private static final int BATCH_SIZE = 20;

    private Class<? extends Pipe> pipeClazz;

    private final Class<?> sinkClazz;

    private String sinkJson;

    private ObjectPump<?> sourcePump;

    private transient Repository r;

    private transient Pipe pipe;

    private transient Object sink;

    private transient IdRef<?> sinkId;

    public ReflowFluxTask(Pipe pipe, Object sink) {
        this(pipe, sink, null);
    }

    public ReflowFluxTask(Pipe pipe, Object sink, ObjectPump<?> sourcePump) {
        this.pipeClazz = pipe.getClass();
        this.sinkClazz = sink.getClass();
        this.sinkJson = JsonUtils.to(sink);
        this.sourcePump = sourcePump;
    }

    @Override
    public void run() {
        init();

        try {
            fluxSourcesToSink();
        } catch (IllegalStateException e) {
            logger.warning(e.getMessage());
            return;
        }
    }

    private void init() {
        this.r = yawp();
        this.pipe = newPipeInstance();
        this.sink = JsonUtils.from(r, sinkJson, sinkClazz);
        this.sinkId = new ObjectHolder(sink).getId();
        initSourcePump();
    }

    private void initSourcePump() {
        if (sourcePump != null) {
            return;
        }
        pipe.configureSources(sink);
        sourcePump = pipe.getSourcePump();
    }

    private void fluxSourcesToSink() {
        if (!sourcePump.hasMore()) {
            return;
        }

        List<?> sources = sourcePump.more();

        if (sourcePump.hasMore()) {
            enqueueNextBatch();
        }

        fluxSources(sources);
    }

    private void fluxSources(Collection<?> sources) {
        Queue queue = QueueHelper.getPipeQueue();

        for (Object source : sources) {
            Pipe pipe = newPipeInstance();
            pipe.configureSinks(source);

            Set sinks = pipe.allSinks();

            if (!sinks.contains(sinkId)) {
                continue;
            }

            pipe.forceSink(sinkId);
            r.driver().pipes().flux(pipe, source);
        }
    }

    private void enqueueNextBatch() {
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(TaskOptions.Builder.withPayload(new ReflowFluxTask(pipe, sink, sourcePump)));
    }

    private Pipe newPipeInstance() {
        return Pipe.newInstance(r, pipeClazz);
    }
}
