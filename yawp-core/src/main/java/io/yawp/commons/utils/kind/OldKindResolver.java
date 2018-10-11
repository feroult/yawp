package io.yawp.commons.utils.kind;

import io.yawp.repository.annotations.Endpoint;

public class OldKindResolver extends KindResolver {

    @Override
    public String getKind(Class<?> clazz) {
        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        if (endpoint == null) {
            throw new RuntimeException("Invalid entity class " + clazz.getSimpleName() + "; @Endpoint annoation required for this clazz.");
        }
        return endpoint.path();
    }

    @Override
    public String getPath(String kind) {
        return kind;
    }

}
