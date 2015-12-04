package io.yawp.repository;

import io.yawp.repository.actions.ActionKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RepositoryFeatures {

    private Map<Class<?>, EndpointFeatures<?>> endpoints;

    private Map<String, Class<?>> paths;

    public RepositoryFeatures(Collection<EndpointFeatures<?>> endpoints) {
        this.endpoints = new HashMap<>();
        this.paths = new HashMap<>();
        for (EndpointFeatures<?> endpoint : endpoints) {
            this.endpoints.put(endpoint.getClazz(), endpoint);
            String endpointPath = endpoint.getEndpointPath();
            if (!endpointPath.isEmpty()) {
                if (paths.get(endpointPath) != null) {
                    throw new RuntimeException("Repeated io.yawp path " + endpointPath + " for class "
                            + endpoint.getClazz().getSimpleName() + " (already found in class " + paths.get(endpointPath).getSimpleName()
                            + ")");
                }
                if (!isValidEndpointPath(endpointPath)) {
                    throw new RuntimeException("Invalid io.yawp path " + endpointPath + " for class " + endpoint.getClazz().getSimpleName());
                }
                paths.put(endpointPath, endpoint.getClazz());
            }
        }

    }

    protected RepositoryFeatures() {
    }

    protected boolean isValidEndpointPath(String endpointName) {
        char[] charArray = endpointName.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (i == 0) {
                if (c != '/') {
                    return false;
                }
                continue;
            }
            if (!(Character.isAlphabetic(c) || c == '_' || c == '-')) {
                return false;
            }
        }
        return true;
    }

    public EndpointFeatures<?> get(Class<?> clazz) {
        return endpoints.get(clazz);
    }

    public EndpointFeatures<?> get(String endpointPath) {
        Class<?> clazz = paths.get(endpointPath);
        if (clazz == null) {
            throw new EndpointNotFoundException(endpointPath);
        }
        return get(clazz);
    }

    public boolean hasCustomAction(String endpointPath, ActionKey actionKey) {
        EndpointFeatures<?> endpointFeatures = get(endpointPath);
        return endpointFeatures.hasCustomAction(actionKey);
    }

    public boolean hasCustomAction(Class<?> clazz, ActionKey actionKey) {
        EndpointFeatures<?> endpointFeatures = get(clazz);
        return endpointFeatures.hasCustomAction(actionKey);
    }

    public Set<Class<?>> getEndpointClazzes() {
        return endpoints.keySet();
    }
}
