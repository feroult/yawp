package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/basic_objects_counter")
public class BasicObjectCounter {

    @Id
    private IdRef<BasicObjectCounter> id;

    private Integer count = 0;

    public IdRef<BasicObjectCounter> getId() {
        return id;
    }

    public void setId(IdRef<BasicObjectCounter> id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void inc() {
        count++;
    }

    public void dec() {
        count--;
    }
}
