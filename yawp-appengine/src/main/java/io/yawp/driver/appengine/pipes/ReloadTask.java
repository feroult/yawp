package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.taskqueue.DeferredTask;

public class ReloadTask implements DeferredTask {

    private Class<?> pipeClazz;

    private String cursor;

    public ReloadTask(Class<?> pipeClazz) {
        this.pipeClazz = pipeClazz;
    }

    @Override
    public void run() {

        


    }
}
