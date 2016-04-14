package io.yawp.repository.pipes.basic;

import io.yawp.repository.models.basic.PipedObjectCounter;
import io.yawp.repository.models.basic.PipedObjectCounterSum;
import io.yawp.repository.pipes.Pipe;

public class CounterSumPipe extends Pipe<PipedObjectCounter, PipedObjectCounterSum> {

    @Override
    public void configureSinks(PipedObjectCounter source) {
        addSinkId(id(PipedObjectCounterSum.class, 1L));
    }

    @Override
    public void flux(PipedObjectCounter counter, PipedObjectCounterSum sum) {
        sum.add(counter.getCount());
    }

    @Override
    public void reflux(PipedObjectCounter counter, PipedObjectCounterSum sum) {
        sum.subtract(counter.getCount());
    }
}
