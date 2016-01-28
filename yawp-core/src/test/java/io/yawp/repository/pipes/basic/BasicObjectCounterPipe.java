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
    public void flux(final BasicObject object, final BasicObjectCounter counter) {
        counter.inc();

        if (isGroup(object, "group-a")) {
            counter.incGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.incGroupB();
        }
    }

    @Override
    public void reflux(BasicObject object, BasicObjectCounter counter) {
        counter.dec();

        if (isGroup(object, "group-a")) {
            counter.decGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.decGroupB();
        }
    }

    private boolean isGroup(BasicObject object, String groupName) {
        String text = object.getStringValue();
        if (text == null) {
            return false;
        }
        return text.equals(groupName);
    }

}
