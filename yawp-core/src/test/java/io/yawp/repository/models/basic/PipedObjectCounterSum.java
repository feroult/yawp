package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/piped_object_counter_sums")
public class PipedObjectCounterSum {

    @Id
    private IdRef<PipedObjectCounterSum> id;

    private Integer sum = 0;

    public IdRef<PipedObjectCounterSum> getId() {
        return id;
    }

    public void setId(IdRef<PipedObjectCounterSum> id) {
        this.id = id;
    }

    public Integer getSum() {
        return sum;
    }

    public void add(Integer count) {
        sum += count;
    }

    public void subtract(Integer count) {
        sum -= count;
    }
}
