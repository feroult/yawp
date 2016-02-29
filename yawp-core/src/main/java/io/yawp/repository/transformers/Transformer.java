package io.yawp.repository.transformers;

import io.yawp.repository.Feature;

/**
 * Transformer API
 * <p/>
 * The Transformer API is used to create different views of the same
 * endpoint model. It can be used to add or hide information from the
 * response.
 * <p/>
 * All subclasses of this base class will be scanned at the bootstrap
 * of the framework.
 * <p/>
 * To create a custom transformer, add a method that has only one argument,
 * which has to be a reference to the parametrized endpoint model.
 * The method will return any other object instance, that will contain
 * the desired transformations.
 * <p/>
 * The add transformation to the default rest actions, override
 * the methods of this base class
 *
 * @param <T> The endpoint model type.
 */
public abstract class Transformer<T> extends Feature {

    /**
     * Override this method to transform all rest responses for
     * the parametrized endpoint model.
     *
     * @param object The endpoint model object to be transformed.
     * @return The transformed object for the response.
     */
    public Object defaults(T object) {
        return object;
    }

    /**
     * Override this method to transform the index rest responses for
     * the parametrized endpoint model.
     * This method has priority over {@link #defaults(T)}.
     *
     * @param object The endpoint model object to be transformed.
     * @return The transformed object for the response.
     */
    public Object index(T object) {
        return defaults(object);
    }

    /**
     * Override this method to transform the show rest responses for
     * the parametrized endpoint model.
     * This method has priority over {@link #defaults(T)}.
     *
     * @param object The endpoint model object to be transformed.
     * @return The transformed object for the response.
     */
    public Object show(T object) {
        return defaults(object);
    }

    /**
     * Override this method to transform the create rest responses for
     * the parametrized endpoint model.
     * This method has priority over {@link #defaults(T)}.
     *
     * @param object The endpoint model object to be transformed.
     * @return The transformed object for the response.
     */
    public Object create(T object) {
        return defaults(object);
    }

    /**
     * Override this method to transform the update rest responses for
     * the parametrized endpoint model.
     * This method has priority over {@link #defaults(T)}.
     *
     * @param object The endpoint model object to be transformed.
     * @return The transformed object for the response.
     */
    public Object update(T object) {
        return defaults(object);
    }

    /**
     * Override this method to transform all custom actions rest responses
     * for the parametrized endpoint model.
     * This method has priority over {@link #defaults(T)}.
     *
     * @param object The endpoint model object to be transformed.
     * @return The transformed object for the response.
     */
    public Object custom(T object) {
        return defaults(object);
    }

}
