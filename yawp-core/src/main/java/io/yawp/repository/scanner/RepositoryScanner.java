package io.yawp.repository.scanner;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.actions.Action;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.transformers.Transformer;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class RepositoryScanner {

    private final static Logger LOGGER = Logger.getLogger(RepositoryScanner.class.getName());

    private boolean enableHooks;

    private Reflections endpointsPackage;

    private Map<Class<?>, EndpointFeatures<?>> endpoints;

    private Map<Class<?>, EndpointTree<?>> endpointTrees;

    public RepositoryScanner(String packagePrefix) {
        this.endpointsPackage = new Reflections(packagePrefix);
        this.endpoints = new HashMap<>();
        this.endpointTrees = new HashMap<>();
        this.enableHooks = true;
    }

    public RepositoryFeatures scan() {
        long start = System.currentTimeMillis();
        RepositoryFeatures repositoryFeatures = new RepositoryFeatures(generateEndpointsMap());
        long elapsed = System.currentTimeMillis() - start;
        LOGGER.info("Yawp! started in " + elapsed + " ms");
        return repositoryFeatures;
    }

    private Map<Class<?>, EndpointFeatures<?>> generateEndpointsMap() {
        endpointsScan();
        featuresScan();
        featuresLoad();
        return endpoints;
    }

    private void featuresScan() {
        scanActions();
        scanTransformers();
        if (enableHooks) {
            scanHooks();
            scanShields();
        }
    }

    private void featuresLoad() {
        for (Class<?> endpointClazz : endpointTrees.keySet()) {
            EndpointFeatures<?> endpoint = endpoints.get(endpointClazz);

            EndpointTree<?> tree = endpointTrees.get(endpointClazz);
            endpoint.setActions(tree.loadActions());
            endpoint.setHooks(tree.loadHooks());
            endpoint.setTransformers(tree.loadTransformers());
            endpoint.setShieldInfo(tree.loadShield());
        }
    }

    private void endpointsScan() {
        Set<Class<?>> clazzes = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);

        for (Class<?> endpointClazz : clazzes) {
            endpoints.put(endpointClazz, new EndpointFeatures<>(endpointClazz));
            endpointTrees.put(endpointClazz, new EndpointTree(endpointClazz));
        }
    }

    private <T> List<EndpointFeatures<? extends T>> getEndpoints(Class<T> objectClazz, String featureClazz) {
        List<EndpointFeatures<? extends T>> list = new ArrayList<>();
        for (Class<?> endpoint : endpoints.keySet()) {
            if (isEndpointInTheHierarchy(endpoint, objectClazz)) {
                list.add((EndpointFeatures<T>) endpoints.get(endpoint));
            }
        }
        if (list.isEmpty()) {
            throw new RuntimeException("Tryed to create feature '" + featureClazz + "' with entity '" + objectClazz.getName()
                    + "' that is not an @Endpoint nor a super class of one.");
        }
        return list;
    }

    private <T> boolean isEndpointInTheHierarchy(Class<?> endpoint, Class<T> objectClazz) {
        return objectClazz.isAssignableFrom(endpoint);
    }

    private void scanActions() {
        Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

        for (Class<? extends Action> actionClazz : clazzes) {
            if (Modifier.isAbstract(actionClazz.getModifiers())) {
                continue;
            }

            addActionToEndpoints(actionClazz);
        }
    }

    private void addActionToEndpoints(Class<? extends Action> actionClazz) {
        Class<?> objectClazz = ReflectionUtils.getFeatureEndpointClazz(actionClazz);

        if (objectClazz == null) {
            return;
        }

        for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, actionClazz.getSimpleName())) {
            endpointTrees.get(endpoint.getClazz()).addAction(actionClazz);
        }
    }

    private void scanTransformers() {
        Set<Class<? extends Transformer>> clazzes = endpointsPackage.getSubTypesOf(Transformer.class);

        for (Class<? extends Transformer> transformerClazz : clazzes) {
            if (Modifier.isAbstract(transformerClazz.getModifiers())) {
                continue;
            }

            addTransformerToEndpoints(transformerClazz);
        }
    }

    private void addTransformerToEndpoints(Class<? extends Transformer> transformerClazz) {
        Class<?> objectClazz = ReflectionUtils.getFeatureEndpointClazz(transformerClazz);

        if (objectClazz == null) {
            return;
        }

        for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, transformerClazz.getSimpleName())) {
            endpointTrees.get(endpoint.getClazz()).addTransformer(transformerClazz);
        }
    }

    private void scanHooks() {
        Set<Class<? extends Hook>> clazzes = endpointsPackage.getSubTypesOf(Hook.class);

        for (Class<? extends Hook> hookClazz : clazzes) {
            if (Modifier.isAbstract(hookClazz.getModifiers())) {
                continue;
            }

            addHookToEndpoints(hookClazz);
        }
    }

    private <T, V extends Hook<T>> void addHookToEndpoints(Class<V> hookClazz) {
        Class<T> objectClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(hookClazz);

        if (objectClazz == null) {
            return;
        }

        for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, hookClazz.getSimpleName())) {
            endpointTrees.get(endpoint.getClazz()).addHook(hookClazz);
        }
    }


    private void scanShields() {
        Set<Class<? extends Shield>> clazzes = endpointsPackage.getSubTypesOf(Shield.class);

        for (Class<? extends Shield> shieldClazz : clazzes) {
            if (Modifier.isAbstract(shieldClazz.getModifiers())) {
                continue;
            }

            addShieldToEndpoints(shieldClazz);
        }
    }

    private <T, V extends Shield<T>> void addShieldToEndpoints(Class<V> shieldClazz) {
        Class<T> objectClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(shieldClazz);

        if (objectClazz == null) {
            return;
        }

        for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, shieldClazz.getSimpleName())) {
            endpointTrees.get(endpoint.getClazz()).addShield(shieldClazz);
        }
    }

    public RepositoryScanner enableHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
        return this;
    }

}
