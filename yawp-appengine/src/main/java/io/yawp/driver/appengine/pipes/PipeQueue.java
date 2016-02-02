package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.taskqueue.DeferredTask;

public class PipeQueue implements DeferredTask {

    private Payload payload;

    public PipeQueue(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void run() {
        System.out.println("here in fork queue");
    }

}
