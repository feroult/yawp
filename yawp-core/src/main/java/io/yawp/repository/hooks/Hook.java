package io.yawp.repository.hooks;

import io.yawp.repository.features.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

/**
 * Hook API
 * <p/>
 * The Hook API is used to intercept and add custom logic to the default
 * lifecycle of an endpoint model from the request down to the repository.
 * <p/>
 * All subclasses of this base class will be scanned at the bootstrap
 * of the framework.
 *
 * @param <T> The endpoint model type.
 */
public class Hook<T> extends Feature {

    /**
     * Override this method to be invoked before the {@link io.yawp.repository.shields.Shield}
     * is applied.
     *
     * @param object The endpoint model object being hooked.
     */
    public void beforeShield(T object) {
    }

    /**
     * Override this method to be invoked right before the endpoint model
     * is saved.
     *
     * @param object The endpoint model object being hooked.
     */
    public void beforeSave(T object) {
    }

    /**
     * Override this method to be invoked right after the endpoint model
     * is saved.
     *
     * @param object The endpoint model object being hooked.
     */
    public void afterSave(T object) {
    }

    /**
     * Override this method to be invoked right before a query is executed
     * to this endpoint model.
     *
     * @param obj The {@link BeforeQueryObject<T>} containing the {@link QueryBuilder<T>} and the query type.
     */
    public void beforeQuery(BeforeQueryObject<T> obj) {
    }

    /**
     * Override this method to be invoked right after a query is executed,
     * if that query ends up with a executeQuery call.
     * @param obj an object containing the query performed and the result.
     */
    public void afterQuery(AfterQueryListObject<T> obj) {
    }

    /**
     * Override this method to be invoked right after a query is executed,
     * if that query ends up with a fetch by id operation.
     * @param obj an object containing the query performed and the result.
     */
    public void afterQuery(AfterQueryFetchObject<T> obj) {
    }

    /**
     * Override this method to be invoked right after a query is executed,
     * if that query ends up with an ids only call.
     * @param obj an object containing the query performed and the result.
     */
    public void afterQuery(AfterQueryIdsObject<T> obj) {
    }

    /**
     * Override this method to be invoked right before the endpoint model
     * is destroyed.
     *
     * @param object The endpoint model object being hooked.
     */
    public void beforeDestroy(IdRef<T> object) {
    }

    /**
     * Override this method to be invoked right after the endpoint model
     * is saved.
     *
     * @param object The endpoint model object being hooked.
     */
    public void afterDestroy(IdRef<T> object) {
    }
}
