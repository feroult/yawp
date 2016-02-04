package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.taskqueue.DeferredTask;

public class JoinTask implements DeferredTask {

    private Integer index;

    private Payload payload;

    public JoinTask(Integer index, Payload payload) {
        this.index = index;
        this.payload = payload;
    }

    @Override
    public void run() {

    }
}
