package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

@Endpoint(path = "/piped_object_counters")
public class PipedObjectCounter {

    @Id
    private IdRef<PipedObjectCounter> id;

    private Integer count = 0;

    private Integer countGroupA = 0;

    private Integer countGroupB = 0;

    @Index
    private boolean active = false;

    public IdRef<PipedObjectCounter> getId() {
        return id;
    }

    public void setId(IdRef<PipedObjectCounter> id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountGroupA() {
        return countGroupA;
    }

    public Integer getCountGroupB() {
        return countGroupB;
    }

    public void setCountGroupA(Integer countGroupA) {
        this.countGroupA = countGroupA;
    }

    public void setCountGroupB(Integer countGroupB) {
        this.countGroupB = countGroupB;
    }

    public void inc() {
        count++;
    }

    public void dec() {
        count--;
    }

    public void incGroupA() {
        countGroupA++;
    }

    public void decGroupA() {
        countGroupA--;
    }

    public void incGroupB() {
        countGroupB++;
    }

    public void decGroupB() {
        countGroupB--;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
