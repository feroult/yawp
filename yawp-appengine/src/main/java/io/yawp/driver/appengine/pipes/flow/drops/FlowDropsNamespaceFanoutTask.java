package io.yawp.driver.appengine.pipes.flow.drops;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;

import static com.google.appengine.api.datastore.Entities.NAMESPACE_METADATA_KIND;

public class FlowDropsNamespaceFanoutTask implements DeferredTask {

    private static final int BATCH_SIZE = 100;

    private String cursor;

    private transient DatastoreService datastore;

    public FlowDropsNamespaceFanoutTask() {
    }

    public FlowDropsNamespaceFanoutTask(String cursor) {
        this.cursor = cursor;
    }

    @Override
    public void run() {
        init();
        fanout();
    }

    private void init() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private void fanout() {
        QueryResultList<Entity> entities = getNamespaces();

        if (entities.size() >= BATCH_SIZE) {
            enqueueNextBatch(entities.getCursor().toWebSafeString());
        }

        for (Entity entity : entities) {
            enqueuePipeDropsTask(entity);
        }
    }

    private QueryResultList<Entity> getNamespaces() {
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        fetchOptions.limit(BATCH_SIZE);

        if (cursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
        }

        Query q = new Query(NAMESPACE_METADATA_KIND);
        q.setKeysOnly();

        return datastore.prepare(q).asQueryResultList(fetchOptions);
    }

    private void enqueuePipeDropsTask(Entity entity) {
        String ns = null;
        if (entity.getKey().getId() == 0) {
            ns = entity.getKey().getName();
        }

        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new FlowDropsTask(ns)));
    }

    private void enqueueNextBatch(String cursor) {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new FlowDropsNamespaceFanoutTask(cursor)));
    }

}
