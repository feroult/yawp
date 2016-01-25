package io.yawp.repository.pipes;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;

import java.util.HashSet;
import java.util.Set;

public abstract class Pipe<T, S> extends Feature {

    private T source;

    private Set<IdRef<S>> sinks = new HashSet<>();

    public abstract void configure(T source);

    public abstract void flux(T source, S sink);

    public abstract void reflux(T source, S sink);

    public void init(T source) {
        this.source = source;
        configure(source);
    }

    protected void addSink(IdRef<S> id) {
        sinks.add(id);
    }
}
