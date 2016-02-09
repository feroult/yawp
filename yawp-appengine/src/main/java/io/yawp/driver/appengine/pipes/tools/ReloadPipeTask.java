package io.yawp.driver.appengine.pipes.tools;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.driver.appengine.pipes.helpers.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class ReloadPipeTask implements DeferredTask {

    private static final int CHUNK_SIZE = 10;

    private Class<? extends Pipe> pipeClazz;

    private String cursor;

    private transient Class<?> sinkClazz;

    public ReloadPipeTask(Class<? extends Pipe> pipeClazz, String cursor) {
        this.pipeClazz = pipeClazz;
        this.cursor = cursor;
    }

    public ReloadPipeTask(Class<? extends Pipe> pipeClazz) {
        this(pipeClazz, null);
    }

    @Override
    public void run() {
        init();
        execute();
    }

    private void init() {
        sinkClazz = ReflectionUtils.getFeatureTypeArgumentAt(pipeClazz, 1);
    }

    private void execute() {
        QueryBuilder<?> q = createQuery(sinkClazz);
        List<IdRef<?>> ids = getIds(q);

        enqueueNext(q, ids);

        for (IdRef<?> id : ids) {
            reloadSink(id);
        }
    }

    private List<IdRef<?>> getIds(QueryBuilder<?> q) {
        List<? extends IdRef<?>> ids = q.ids();
        return (List<IdRef<?>>) ids;
    }

    private void reloadSink(IdRef<?> id) {
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(TaskOptions.Builder.withPayload(new ClearSinkTask(pipeClazz, id.getUri())));
    }

    private QueryBuilder<?> createQuery(Class<?> sinkClazz) {
        return yawp(sinkClazz).cursor(cursor).limit(CHUNK_SIZE);
    }

    private void enqueueNext(QueryBuilder<?> q, List<IdRef<?>> ids) {
        if (ids.size() != CHUNK_SIZE) {
            return;
        }
        enqueueNext(q.getCursor());
    }

    private void enqueueNext(String cursor) {
        Queue queue = QueueHelper.getPipeQueue();
        queue.add(TaskOptions.Builder.withPayload(new ReloadPipeTask(pipeClazz, cursor)));
    }
}
