package io.yawp.repository.pipes;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.features.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.pipes.pump.IdPump;
import io.yawp.repository.pipes.pump.PumpGenerator;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.Set;

/**
 * Pipe API
 * <p/>
 * The Pipe API is used to create an asynchronous information flow from
 * one endpoint model (source) to another (sink). It can be used to
 * create a variety of aggregation models without creating scalability
 * bottle-necks.
 *
 * @param <T> The source endpoint model type.
 * @param <S> The sink endpoint model type.
 */
public abstract class Pipe<T, S> extends Feature {

    private static final int BATCH_SIZE = 30;

    private Class<T> sourceClazz;

    private Class<S> sinkClazz;

    private IdPump<T> sourcePump;

    private IdPump<S> sinkPump;

    public static Pipe newInstance(Repository r, Class<? extends Pipe> pipeClazz) {
        try {
            Class<?> sourceClazz = ReflectionUtils.getFeatureEndpointClazz(pipeClazz);
            Class<?> sinkClazz = ReflectionUtils.getFeatureTypeArgumentAt(pipeClazz, 1);

            Pipe pipe = pipeClazz.newInstance();
            pipe.setRepository(r);
            pipe.init(sourceClazz, sinkClazz);
            return pipe;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public final void init(Class<T> sourceClazz, Class<S> sinkClazz) {
        this.sourceClazz = sourceClazz;
        this.sinkClazz = sinkClazz;
        this.sourcePump = new IdPump<>(sourceClazz, BATCH_SIZE);
        this.sinkPump = new IdPump<>(sinkClazz, BATCH_SIZE);
    }

    /**
     * Override this method to specify a custom default queue for this pipe.
     *
     * @return Queue name
     */
    public String getDefaultQueue() {
        return null;
    }

    /**
     * Override this method to specify a custom fork queue for this pipe.
     *
     * @return Queue name
     */
    public String getForkQueue() {
        return null;
    }

    /**
     * Override this method to specify a custom join queue for this pipe.
     *
     * @return Queue name
     */
    public String getJoinQueue() {
        return null;
    }

    /**
     * Override this method to specify a custom reflow queue for this pipe.
     *
     * @return Queue name
     */
    public String getReflowQueue() {
        return null;
    }

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
        sinkPump.add(id);
    }

    /**
     * Call this method from {@link #configureSinks(T)} to add list of sink ids
     * for a given source.
     * <p/>
     * <b>Note:</b> the sink ids should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param ids The sink ids.
     */
    public final void addSinkIds(List<IdRef<S>> ids) {
        sinkPump.addAll(ids);
    }

    /**
     * Call this method from {@link #configureSinks(T)} to add a query of sink ids
     * for a given source.
     * <p/>
     * <b>Note:</b> the sink ids should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param query The {@link QueryBuilder<S>} to query for sink ids.
     */
    public final void addSinkIdsQuery(QueryBuilder<S> query) {
        sinkPump.addQuery(query);
    }

    /**
     * Call this method from {@link #configureSinks(T)} to add a {@link PumpGenerator<IdRef<S>>}
     * of sink ids for a given source.
     * <p/>
     * <b>Note:</b> the sink ids should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param generator The generator.
     */
    public final void addSinkIdsGenerator(PumpGenerator<IdRef<S>> generator) {
        sinkPump.addGenerator(generator);
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
     * Call {@link #addSourceId(IdRef<T>)}, {@link #addSourceIds(List<T>)} or
     * {@link #addSourceIdsQuery(QueryBuilder<T>)} to specify which sources should be
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
     * source ids to be fluxed when the specified sink is reflowed.
     * <p/>
     * <b>Note:</b> the source should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param source The source id.
     */
    public void addSourceId(IdRef<T> source) {
        sourcePump.add(source);
    }

    /**
     * Call this method from {@link #configureSources(S)} to add a list of
     * source ids to be fluxed when the specified sink is reflowed.
     * <p/>
     * <b>Note:</b> the sources should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param sources The list of source objects.
     */
    public void addSourceIds(List<IdRef<T>> sources) {
        sourcePump.addAll(sources);
    }

    /**
     * Call this method from {@link #configureSources(S)} to add a query for
     * source ids to be fluxed when the specified sink is reflowed.
     * <p/>
     * <b>Note:</b> this query should be strong consistent (ancestor query
     * in GAE), otherwise the pipe may become inconsistent.
     *
     * @param query The {@link QueryBuilder<T>} to query for sources.
     */
    public void addSourceIdsQuery(QueryBuilder<T> query) {
        sourcePump.addQuery(query);
    }

    /**
     * Call this method from {@link #configureSources(S)} to add a {@link PumpGenerator<IdRef<T>>}
     * of source ids for a given sink.
     * <p/>
     * <b>Note:</b> the sources should be retrieved in a strong consistent way
     * (ancestor query or key fetch in GAE), otherwise the pipe may become
     * inconsistent.
     *
     * @param generator The generator.
     */
    public void addSourceIdsGenerator(PumpGenerator<IdRef<T>> generator) {
        sourcePump.addGenerator(generator);
    }

    /**
     * Override this method to empty the sink before it is reloaded.
     *
     * @param sink The sink object.
     */
    public void drain(S sink) {
    }

    public final Set<IdRef<S>> allSinks() {
        return sinkPump.all();
    }

    public final boolean hasSinks() {
        return sinkPump.hasMore();
    }

    public final void forceSink(IdRef<S> sinkId) {
        sinkPump = new IdPump<>(sinkClazz, BATCH_SIZE);
        sinkPump.add(sinkId);
    }

    public IdPump<T> getSourcePump() {
        return sourcePump;
    }
}
