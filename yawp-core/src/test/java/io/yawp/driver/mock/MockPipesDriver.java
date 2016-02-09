package io.yawp.driver.mock;

import io.yawp.driver.api.PipesDriver;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.query.NoResultException;

import java.util.Set;

public class MockPipesDriver implements PipesDriver {

    private Repository r;

    public MockPipesDriver(Repository r) {
        this.r = r;
    }

    @Override
    public void flux(Pipe pipe, Object object) {
        Set<IdRef<?>> sinks = pipe.getSinks();

        for (IdRef<?> sinkId : sinks) {
            Object sink = fetchOrCreateSink(sinkId);
            pipe.flux(object, sink);
            r.driver().persistence().save(sink);
        }
    }

    @Override
    public void reflux(Pipe pipe, Object object) {
        Set<IdRef<?>> sinks = pipe.getSinks();

        for (IdRef<?> sinkId : sinks) {
            Object sink = fetchOrCreateSink(sinkId);
            pipe.reflux(object, sink);
            r.driver().persistence().save(sink);
        }
    }

    @Override
    public void reload(Class<?> pipeClazz) {

    }

    private Object fetchOrCreateSink(IdRef<?> id) {
        try {
            return id.fetch();
        } catch (NoResultException e) {
            return createSink(id);
        }
    }

    private Object createSink(IdRef<?> id) {
        try {
            Object sink = id.getClazz().newInstance();
            ObjectHolder holder = new ObjectHolder(sink);
            holder.setId(id);
            return sink;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
