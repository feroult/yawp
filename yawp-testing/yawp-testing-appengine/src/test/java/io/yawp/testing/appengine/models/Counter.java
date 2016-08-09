package io.yawp.testing.appengine.models;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/counters")
public class Counter {

    @Id
    private IdRef<Counter> id;

    private long count;

    public void inc() {
        this.count++;
    }

    public void dec() {
        this.count--;
    }

    public long getCount() {
        return count;
    }
}
