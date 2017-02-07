package io.yawp.testing.appengine;

import com.google.appengine.api.urlfetch.URLFetchServicePb;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class TestingTaskQueueCallback extends LocalTaskQueueTestConfig.DeferredTaskCallback {

    @Override
    public int execute(URLFetchServicePb.URLFetchRequest req) {
        return super.execute(req);
    }
}
