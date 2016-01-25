package io.yawp.repository.pipes.basic;

import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.BasicObjectCounter;
import io.yawp.repository.pipes.Pipe;

public class BasicObjectCounterPipe extends Pipe<BasicObject, BasicObjectCounter> {

    @Override
    public void configure(BasicObject object) {
        addSink(id(BasicObjectCounter.class, 1L));
    }

    @Override
    public void flux(BasicObject object, BasicObjectCounter counter) {
        counter.inc();
    }

    @Override
    public void reflux(BasicObject object, BasicObjectCounter counter) {
        counter.dec();
    }

}
