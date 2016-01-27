package io.yawp.repository.pipes.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.BasicObjectCounter;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.pipes.Sink;
import io.yawp.repository.pipes.Source;

public class BasicObjectCounterPipe extends Pipe<BasicObject, BasicObjectCounter> {

    @Override
    public void configure(BasicObject object) {
        addSink(id(BasicObjectCounter.class, 1L));
    }

    @Override
    public void configure(IdRef<BasicObject> sourceId) {
        addSink(id(BasicObjectCounter.class, 1L));
    }

    @Override
    public void flux(BasicObject object, BasicObjectCounter counter) {


        Source<BasicObject> source = new Source<>();
        Sink<BasicObjectCounter> sink = new Sink<>(counter);


        //sink.to("count").inc();


        //sink.flow("inc");


        //sink.

        if (sourceAdded()) {
            counter.inc();
        }

        sink.added().inc();


        //addedInSink(object, "group", "group-a");

        if (isGroup(object, "group-a")) {
            if (!rememberInSink("group", "group-a")) {
                counter.incGroupA();
            }
        }

        if (isGroup(object, "group-b")) {
            //sink.remember("group").as("group-a").incGroupA();
            counter.incGroupB();
        }
    }

    @Override
    public void reflux(BasicObjectCounter counter, IdRef<BasicObject> objectId) {

        Sink<BasicObjectCounter> sink = new Sink<>(counter);

        if (sourceRemoved()) {
            counter.dec();
        }

        //sink.removed().dec();

        counter.dec();

        BasicObject object = objectId.fetch();

        if (isGroup(object, "group-a")) {
            counter.decGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.decGroupB();
        }
    }

    private boolean rememberInSink(String group, String s) {
        return false;
    }

    private boolean sourceRemoved() {
        return false;
    }

    private boolean sourceAdded() {
        return false;
    }

    private boolean isGroup(BasicObject object, String groupName) {
        String text = object.getStringValue();
        if (text == null) {
            return false;
        }
        return text.equals(groupName);
    }

}
