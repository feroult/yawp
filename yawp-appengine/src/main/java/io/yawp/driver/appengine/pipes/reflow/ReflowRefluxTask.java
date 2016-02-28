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
import io.yawp.repository.pipes.SinkMarker;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.logging.Logger;

import static io.yawp.repository.Yawp.yawp;

public class ReflowRefluxTask implements DeferredTask {
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

    public ReflowRefluxTask(Pipe pipe, Object sink) {
        this(pipe, sink, null);
    }

    public ReflowRefluxTask(Pipe pipe, Object sink, String cursor) {
        this.pipeClazz = pipe.getClass();
        this.sinkClazz = sink.getClass();
        this.sinkJson = JsonUtils.to(sink);
        this.cursor = cursor;
    }

    @Override
    public void run() {
        init();

        try {
            refluxSourcesFromSink();
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
    }

    private void refluxSourcesFromSink() {
        QueryBuilder<SinkMarker> q = prepareQuery();
        List<SinkMarker> sinkMarkers = q.list();

        if (sinkMarkers.size() == BATCH_SIZE) {
            enqueueNextBatch(q.getCursor());
        }

        refluxSources(sinkMarkers);
    }

    private void refluxSources(List<SinkMarker> sinkMarkers) {
        Queue queue = QueueHelper.getPipeQueue();

        for (SinkMarker sinkMarker : sinkMarkers) {
            Object source = sinkMarker.getSourceId().fetch();

            Pipe pipe = newPipeInstance();
            pipe.configureSinks(source);

            if (pipe.containsSink(sinkId)) {
                continue;
            }

            pipe.forceSink(sinkId);
            r.driver().pipes().reflux(pipe, source);
        }
    }

    private void enqueueNextBatch(String nextCursor) {
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(TaskOptions.Builder.withPayload(new ReflowRefluxTask(pipe, sink, nextCursor)));
    }

    private QueryBuilder<SinkMarker> prepareQuery() {
        QueryBuilder<SinkMarker> q = r.query(SinkMarker.class).from(sinkId).order("id").limit(BATCH_SIZE);

        if (cursor != null) {
            q.cursor(cursor);
        }

        return q;
    }

    private void orderById(QueryBuilder q) {
        ObjectModel model = new ObjectHolder(sink).getModel();
        q.order(model.getIdFieldName());
    }

    private Pipe newPipeInstance() {
        return Pipe.newInstance(r, pipeClazz);
    }
}
