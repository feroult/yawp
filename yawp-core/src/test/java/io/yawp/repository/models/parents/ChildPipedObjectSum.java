package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;
import io.yawp.repository.models.basic.BasicObject;

@Endpoint(path = "/child_piped_object_sums")
public class ChildPipedObjectSum {

    @Id
    private IdRef<ChildPipedObjectSum> id;

    @ParentId
    private IdRef<BasicObject> parentId;

    private Integer sum = 0;

    public IdRef<ChildPipedObjectSum> getId() {
        return id;
    }

    public void setId(IdRef<ChildPipedObjectSum> id) {
        this.id = id;
    }

    public IdRef<BasicObject> getParentId() {
        return parentId;
    }

    public void setParentId(IdRef<BasicObject> parentId) {
        this.parentId = parentId;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void add(Integer value) {
        sum += value;
    }

    public void dec(Integer value) {
        sum -= value;
    }
}
