package io.yawp.repository.features.loader;

import io.yawp.repository.features.EndpointFeatures;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TransformerLoader {

    private Map<Class<?>, Map<String, Method>> cache = new HashMap<>();

    public void load(EndpointFeatures<?> endpoint, EndpointTree<?> tree) {
        endpoint.setTransformers(tree.loadTransformers(cache));
    }

}
