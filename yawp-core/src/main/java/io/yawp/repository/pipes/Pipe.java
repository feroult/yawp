package io.yawp.repository.pipes;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pipe API
 * <p/>
 * The Pipe API is used to create an asynchronous information flow from
 * one endpoint model (source) to another (sink). It can be used to
 * create a variety of aggregation models without creating scalability
 * bottle-necks since many sources can be flowed to the same sink.
 *
 * @param <T> The source endpoint model type.
 * @param <S> The sink endpoint model type.
 */
public abstract class Pipe<T, S> extends Feature {

    private Set<IdRef<S>> sinks = new HashSet<>();

    private Set<T> sources = new HashSet<>();

    private QueryBuilder<T> sourcesQuery;

    /**
     * Override this method to configure one or multiple sinks for a given source.
     * <p/>
     * Call {@link #addSinkId(IdRef<S>)} for each sink you want to pipe the source.
     * <p/>
     * <b>Note:</b> the sinkIds should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param source The source that needs to be piped to a sink.
     */
    public abstract void configureSinks(T source);

    /**
     * Call this method from {@link #configureSinks(T)} to add a sink id for
     * a given source.
     * <p/>
     * <b>Note:</b> the sink id should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param id The sink id.
     */
    public final void addSinkId(IdRef<S> id) {
        sinks.add(id);
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
     * Override this method to decide if a sink needs to be reflowed after
     * it is created or updated.
     * <p/>
     * The sink will be reflowed asynchronously by fluxing all sources
     * configured in {@link #configureSources(S)}.
     *
     * @param newSink The sink object containing its new data.
     * @param oldSink The sink object containing its previous data.
     *                It will be null if the sink is being created.
     * @return Whether the sink needs to be reloaded.
     */
    public boolean reflowCondition(S newSink, S oldSink) {
        return false;
    }

    /**
     * Override this method to define configure one or multiple source objects
     * to be fluxed when the specified sink is reflowed.
     * <p/>
     * Call {@link #addSource(T)}, {@link #addSources(List<T>)} or
     * {@link #addSourcesQuery(QueryBuilder<T>)} to specify which sources should be
     * reflowed to the sink.
     * <p/>
     * <b>Note:</b> the sources should be retrieved in a strong consistent way
     * (ancestor query in GAE), otherwise the pipe may become inconsistent.
     *
     * @param sink The sink object.
     */
    public void configureSources(S sink) {
    }

    /**
     * Call this method from {@link #configureSources(S)} to add
     * source object to be fluxed when the specified sink is reflowed.
     * <p/>
     * <b>Note:</b> the source should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param source The source object.
     */
    public void addSource(T source) {
        sources.add(source);
    }

    /**
     * Call this method from {@link #configureSources(S)} to add a list of
     * source objects to be fluxed when the specified sink is reflowed.
     * <p/>
     * <b>Note:</b> the sources should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param sources The list of source objects.
     */
    public void addSources(List<T> sources) {
        sources.addAll(sources);
    }

    /**
     * Call this method from {@link #configureSources(S)} to add a query for
     * source objects to be fluxed when the specified sink is reflowed.
     * <p/>
     * <b>Note:</b> this query should be strong consistent (ancestor query
     * in GAE), otherwise the pipe may become inconsistent.
     *
     * @param query The {@link QueryBuilder<T>} to query for sources.
     */
    public void addSourcesQuery(QueryBuilder<T> query) {
        sourcesQuery = query;
    }

    /**
     * Override this method to empty the sink before it is reloaded.
     *
     * @param sink The sink object.
     */
    public void drain(S sink) {
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

    public final boolean containsSink(IdRef<S> sinkId) {
        return sinks.contains(sinkId);
    }

    public final void forceSink(IdRef<S> sinkId) {
        sinks = new HashSet<>();
        sinks.add(sinkId);
    }

    public final boolean isReflowFromQuery(S sink) {
        // TODO: pumps
        configureSources(sink);
        return sourcesQuery != null;
    }

    public QueryBuilder<T> getSourcesQuery() {
        return sourcesQuery;
    }

    public Set<T> getSources() {
        return sources;
    }
}
