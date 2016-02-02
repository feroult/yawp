package io.yawp.driver.appengine.pipes;

import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.repository.Repository;

public class PipeQueue implements DeferredTask {

    private Payload payload;

    public PipeQueue(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void run() {
        Repository r = Repository.r();

        payload.init(r);

        System.out.println("here in fork queue");
    }

}
