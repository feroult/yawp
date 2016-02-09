package io.yawp.repository.pipes;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;

import java.util.HashSet;
import java.util.Set;

public abstract class Pipe<T, S> extends Feature {

    private Set<IdRef<S>> sinks = new HashSet<>();

    public abstract void configure(T source);

    public abstract void clear(S sink);

    public abstract void flux(T source, S sink);

    public abstract void reflux(T source, S sink);

    protected void addSink(IdRef<S> id) {
        sinks.add(id);
    }

    public Set<IdRef<S>> getSinks() {
        return sinks;
    }
}
