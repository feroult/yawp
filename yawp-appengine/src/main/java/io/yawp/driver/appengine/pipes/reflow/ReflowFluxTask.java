package io.yawp.driver.appengine.pipes.reflow;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.query.QueryBuilder;

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

    private String cursor;

    private transient Repository r;

    private transient Pipe pipe;

    private transient Object sink;

    private transient IdRef<?> sinkId;

    public ReflowFluxTask(Pipe pipe, Object sink) {
        this(pipe, sink, null);
    }

    public ReflowFluxTask(Pipe pipe, Object sink, String cursor) {
        this.pipeClazz = pipe.getClass();
        this.sinkClazz = sink.getClass();
        this.sinkJson = JsonUtils.to(sink);
        this.cursor = cursor;
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
        this.pipe = createPipeInstance();
        this.sink = JsonUtils.from(r, sinkJson, sinkClazz);
        this.sinkId = new ObjectHolder(sink).getId();
    }

    private void fluxSourcesToSink() {
        pipe.configureSources(sink);

        if (isReflowFromQuery()) {
            reflowFromQuery();
        } else {
            reflowFromList();
        }
    }

    private void reflowFromQuery() {
        QueryBuilder q = prepareQuery();
        List<?> sources = q.list();

        if (sources.size() == BATCH_SIZE) {
            enqueueNextBatch(q.getCursor());
        }

        fluxSources(sources);
    }

    private void reflowFromList() {
        Set<?> sources = pipe.getSources();
        fluxSources(sources);
    }

    private void fluxSources(Collection<?> sources) {
        Queue queue = QueueHelper.getPipeQueue();

        for (Object source : sources) {
            Pipe pipe = createPipeInstance();
            pipe.configureSinks(source);

            if (!pipe.containsSink(sinkId)) {
                continue;
            }

            pipe.forceSink(sinkId);
            r.driver().pipes().flux(pipe, source);
        }
    }

    private void enqueueNextBatch(String nextCursor) {
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(TaskOptions.Builder.withPayload(new ReflowFluxTask(pipe, sink, nextCursor)));
    }

    private QueryBuilder<?> prepareQuery() {
        QueryBuilder q = pipe.getSourcesQuery();

        if (q == null) {
            throw new IllegalStateException("Trying to reflow a pipe without overriding sourcesQuery(S sink): " + pipeClazz);
        }

        orderById(q);

        if (cursor != null) {
            q.cursor(cursor);
        }

        q.limit(BATCH_SIZE);
        return q;
    }

    private void orderById(QueryBuilder q) {
        ObjectModel model = new ObjectHolder(sink).getModel();
        q.order(model.getIdFieldName());
    }

    private boolean isReflowFromQuery() {
        return pipe.isReflowFromQuery(sink);
    }

    private Pipe createPipeInstance() {
        try {
            Pipe pipe = pipeClazz.newInstance();
            pipe.setRepository(yawp());
            return pipe;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
