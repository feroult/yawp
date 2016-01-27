package io.yawp.commons.utils.kind;

import io.yawp.repository.annotations.Endpoint;

public class DefaultKindResolver extends KindResolver {

    @Override
    public String getKind(Class<?> clazz) {
        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        if (!endpoint.kind().isEmpty()) {
            return endpoint.kind();
        }
        if (!endpoint.path().isEmpty()) {
            return endpoint.path().substring(1).replaceAll("-", "_");
        }
        return clazz.getSimpleName();
    }

    @Override
    public String getPath(String kind) {
        return "/" + kind;
    }

}
