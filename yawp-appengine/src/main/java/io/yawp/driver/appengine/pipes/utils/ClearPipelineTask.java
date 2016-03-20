package io.yawp.driver.appengine.pipes.utils;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

import java.util.logging.Logger;

import static com.google.appengine.tools.pipeline.JobInfo.*;
import static com.google.appengine.tools.pipeline.JobInfo.State.*;

public class ClearPipelineTask implements DeferredTask {

    private final static Logger logger = Logger.getLogger(ClearPipelineTask.class.getName());

    private static final int RELOAD_PIPELINE_WAIT_MILLIS = 10000;

    private String pipelineId;

    public ClearPipelineTask(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public static void enqueue(String pipelineId) {
        long eta = System.currentTimeMillis() + RELOAD_PIPELINE_WAIT_MILLIS;
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new ClearPipelineTask(pipelineId)).etaMillis(eta));
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (NoSuchObjectException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute() throws NoSuchObjectException {
        PipelineService service = PipelineServiceFactory.newPipelineService();
        State state = getJobInfo(service);
        if (state == COMPLETED_SUCCESSFULLY) {
            logger.info("Cleanig pipeline: " + pipelineId);
            service.deletePipelineRecords(pipelineId);
            return;
        }

        if (state == RUNNING || state == WAITING_TO_RETRY) {
            enqueue(pipelineId);
        }
    }

    private State getJobInfo(PipelineService service) throws NoSuchObjectException {
        JobInfo jobInfo = service.getJobInfo(pipelineId);
        return jobInfo.getJobState();
    }
}
