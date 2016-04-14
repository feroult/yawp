package io.yawp.repository.actions;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.Feature;

/**
 * Action API
 * <p/>
 * The Action API is used to create custom rest actions to manipulate
 * or retrieve the state of a group of endpoint models.
 * <p/>
 * All subclasses of this base class will be scanned at the bootstrap
 * of the framework.
 * <p/>
 * All the public methods annotated with one of the http-verb annotations
 * (GET, POST, PUT, PATCH, DELETE) will be assigned to custom rest routes
 * bellow the path of the parametrized endpoint model.
 *
 * @param <T> The endpoint model type.
 */
public class Action<T> extends Feature {

    /**
     * @deprecated use {@link Feature#to(Object)}.
     */
    @Deprecated
    public String json(Object object) {
        return JsonUtils.to(object);
    }

}
