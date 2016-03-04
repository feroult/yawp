package io.yawp.repository.pipes.parents;

import io.yawp.repository.models.parents.ChildPipedObject;
import io.yawp.repository.models.parents.ChildPipedObjectSum;
import io.yawp.repository.pipes.Pipe;

public class ChildSumPipe extends Pipe<ChildPipedObject, ChildPipedObjectSum> {

    @Override
    public void configureSinks(ChildPipedObject child) {
        addSinkId(child.getSumId());
    }

    @Override
    public void flux(ChildPipedObject child, ChildPipedObjectSum sum) {
        sum.add(child.getValue());
    }

    @Override
    public void reflux(ChildPipedObject child, ChildPipedObjectSum sum) {
        sum.dec(child.getValue());
    }
}
