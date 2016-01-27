package io.yawp.commons.utils.kind;

import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Kind;

public class DefaultKindResolver extends KindResolver {

    @Override
    public String getKind(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Kind.class)) {
            return clazz.getAnnotation(Kind.class).value();
        }

        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        if (endpoint.path() == null) {
            return clazz.getSimpleName();
        }
        return endpoint.path().substring(1).replaceAll("-", "_");
    }

    @Override
    public String getPath(String kind) {
        return "/" + kind;
    }

}
