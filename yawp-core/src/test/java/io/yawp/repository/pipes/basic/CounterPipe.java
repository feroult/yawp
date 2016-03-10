package io.yawp.repository.pipes.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.PipedObject;
import io.yawp.repository.models.basic.PipedObjectCounter;
import io.yawp.repository.pipes.Pipe;

import java.util.List;

public class CounterPipe extends Pipe<PipedObject, PipedObjectCounter> {

    @Override
    public void configureSinks(PipedObject object) {
        IdRef<PipedObjectCounter> counterId = getCounterId(object);
        if (counterId != null) {
            addSinkId(counterId);
        }
    }

    @Override
    public void flux(PipedObject object, PipedObjectCounter counter) {
        counter.inc();

        if (isGroup(object, "group-a")) {
            counter.incGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.incGroupB();
        }
    }

    @Override
    public void reflux(PipedObject object, PipedObjectCounter counter) {
        counter.dec();

        if (isGroup(object, "group-a")) {
            counter.decGroupA();
        }

        if (isGroup(object, "group-b")) {
            counter.decGroupB();
        }
    }

    @Override
    public boolean reflowCondition(PipedObjectCounter newCounter, PipedObjectCounter oldCounter) {
        return oldCounter != null && !oldCounter.isActive() && newCounter.isActive();
    }

    @Override
    public void configureSources(PipedObjectCounter counter) {
        addSourceIdsQuery(yawp(PipedObject.class).where("counterId", "=", counter.getId()));
    }

    @Override
    public void drain(PipedObjectCounter sink) {
        sink.setCount(0);
        sink.setCountGroupA(0);
        sink.setCountGroupB(0);
    }

    private IdRef<PipedObjectCounter> getCounterId(PipedObject object) {
        if (object.getCounterId() != null) {
            return object.getCounterId();
        }
        List<IdRef<PipedObjectCounter>> ids = yawp(PipedObjectCounter.class).where("active", "=", true).ids();
        if (ids.size() == 0) {
            return null;
        }
        return ids.get(0);
    }

    private boolean isGroup(PipedObject object, String groupName) {
        String text = object.getGroup();
        if (text == null) {
            return false;
        }
        return text.equals(groupName);
    }

}
