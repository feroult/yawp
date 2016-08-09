package io.yawp.driver.appengine.pipes.flow.drops;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.flow.CacheHelper;
import io.yawp.driver.appengine.pipes.flow.Work;
import io.yawp.driver.appengine.pipes.flow.WorksExecutor;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static io.yawp.repository.Yawp.yawp;

public class FlowDropsTask implements DeferredTask {

    private static final int BATCH_SIZE = 100;

    public static final int ELAPSED_TIME_MILLES = 1000 * 60 * 20;

    private String ns;

    private String cursor;

    private transient Repository r;

    private transient DatastoreService datastore;

    public FlowDropsTask(String ns) {
        this.ns = ns;
    }

    public FlowDropsTask(String ns, String cursor) {
        this.ns = ns;
        this.cursor = cursor;
    }

    @Override
    public void run() {
        init();
        execute();
    }

    private void init() {
        this.r = yawp().namespace(ns);
    }

    private void execute() {
        List<Work> works = worksQuery().list();
        fanout(works);
        fanin(works);
    }

    private void fanin(List<Work> works) {
        Map<String, List<Work>> worksBySinkGroup = splitWorks(works);

        for (String sinkGroupUri : worksBySinkGroup.keySet()) {
            join(worksBySinkGroup.get(sinkGroupUri));
        }
    }

    private void join(List<Work> worksList) {
        WorksExecutor works = new WorksExecutor(r, worksList);
        works.execute();
        works.destroy();
    }

    private Map<String, List<Work>> splitWorks(List<Work> works) {
        Map<String, List<Work>> worksBySinkGroup = new HashMap<>();
        for (Work work : works) {
            String sinkGroupUri = CacheHelper.getSinkGroupUri(work.getSinkId());

            if (!worksBySinkGroup.containsKey(sinkGroupUri)) {
                worksBySinkGroup.put(sinkGroupUri, new LinkedList<Work>());
            }

            worksBySinkGroup.get(sinkGroupUri).add(work);
        }
        return worksBySinkGroup;
    }

    private void fanout(List<Work> result) {
        if (result.size() >= BATCH_SIZE) {
            enqueueNextBatch(worksQuery().getCursor());
        }
    }

    private QueryBuilder<Work> worksQuery() {
        long timestamp = System.currentTimeMillis() - ELAPSED_TIME_MILLES;

        QueryBuilder<Work> q = yawp(Work.class).where("timestamp", "<=", timestamp);
        if (cursor != null) {
            q.setCursor(cursor);
        }
        q.limit(BATCH_SIZE);
        return q;
    }

    private void enqueueNextBatch(String cursor) {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new FlowDropsTask(ns, cursor)));
    }
}
