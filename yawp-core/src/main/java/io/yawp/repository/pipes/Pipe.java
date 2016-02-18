package io.yawp.repository.pipes;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.HashSet;
import java.util.Set;

public abstract class Pipe<T, S> extends Feature {

    private Set<IdRef<S>> sinks = new HashSet<>();

    /**
     * Override this method to configure multiple sinks for a given source.
     * Call {@link #addSink(IdRef<S>)} for each sink you want to pipe the source.
     *
     * @param source The source that needs to be piped to a sink.
     */
    public void configure(T source) {
        IdRef<S> sinkId = sinkId(source);
        if (sinkId != null) {
            addSink(sinkId);
        }
    }

    /**
     * Override this method when you have only one sink for each source.
     *
     * @param source The source that needs to be piped to a sink.
     * @return The sinkId for the given source.
     */
    public IdRef<S> sinkId(T source) {
        return null;
    }

    /**
     * Override this method to flux information from the source to sink.
     * This method will be invoked asynchronously when the source is created
     * or updated and the source has been associated with the sink.
     *
     * @param source The source object.
     * @param sink   The sink object.
     */
    public abstract void flux(T source, S sink);

    /**
     * Override this method to reflux source information from the sink.
     * This method will be invoked asynchronously when the source is updated
     * or destroyed and the source is no longer associated with the sink.
     *
     * @param source The source object.
     * @param sink   The sink object.
     */
    public abstract void reflux(T source, S sink);


    /**
     * Override this method to empty the sink before it is reloaded.
     *
     * @param sink The sink object.
     */
    public void drain(S sink) {
    }

    /**
     * Override this method to decide if a sink needs to be reloaded after
     * it is created or updated.
     * The sink will be reloaded asynchronously by fluxing all sources
     * returned from {@link #sourcesQuery(Object)}.
     *
     * @param newSink The sink object containing its new data.
     * @param oldSink The sink object containing its previous data.
     *                It will be null if the sink is being created.
     * @return Whether the sink needs to be reloaded.
     */
    public boolean reloadSinkWhen(T newSink, S oldSink) {
        return false;
    }

    /**
     * Override this method to define a query for source objects to be fluxed
     * when the specified sink is reloaded.
     *
     * @param sink The sink object.
     * @return The {@link QueryBuilder<T>} to query for sources.
     */
    public QueryBuilder<T> sourcesQuery(T sink) {
        return null;
    }

    public final void addSink(IdRef<S> id) {
        sinks.add(id);
    }

    public final Set<IdRef<S>> getSinks() {
        return sinks;
    }

    public final void removeSinks(Set sinks) {
        this.sinks.removeAll(sinks);
    }

    public final boolean hasSinks() {
        return sinks.size() != 0;
    }
}
