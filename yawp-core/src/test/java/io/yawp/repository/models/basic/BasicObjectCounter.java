package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/basic_objects_counter")
public class BasicObjectCounter {

    @Id
    private IdRef<BasicObjectCounter> id;

    private Integer count = 0;

    private Integer countGroupA = 0;

    private Integer countGroupB = 0;

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
}
