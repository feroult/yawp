package io.yawp.repository.scanner;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.actions.Action;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.shields.ShieldInfo;
import io.yawp.repository.transformers.Transformer;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class EndpointScanner {

    private final static Logger LOGGER = Logger.getLogger(EndpointScanner.class.getName());

    private boolean enableHooks;

    private Reflections endpointsPackage;

    private Map<Class<?>, EndpointFeatures<?>> endpoints;

    private Map<Class<?>, EndpointTree<?>> endpointTrees;

    public EndpointScanner(String packagePrefix) {
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
        scanEndpoints();
        scanActions();
        scanTransformers();
        if (enableHooks) {
            scanHooks();
        }
        scanShields();

        load();

        return endpoints;
    }

    private void load() {
        for (Class<?> endpointClazz : endpointTrees.keySet()) {
            EndpointFeatures<?> endpoint = endpoints.get(endpointClazz);

            EndpointTree<?> tree = endpointTrees.get(endpointClazz);
            endpoint.setActions(tree.loadActions());
            endpoint.setHooks(tree.loadHooks());
        }
    }

    private void scanEndpoints() {
        Set<Class<?>> clazzes = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);

        for (Class<?> endpointClazz : clazzes) {
            endpoints.put(endpointClazz, new EndpointFeatures<>(endpointClazz));
            endpointTrees.put(endpointClazz, new EndpointTree());
        }
    }

    private void scanShields() {
        Set<Class<? extends Shield>> clazzes = endpointsPackage.getSubTypesOf(Shield.class);

        for (Class<? extends Shield> shieldClazz : clazzes) {
            if (Modifier.isAbstract(shieldClazz.getModifiers())) {
                continue;
            }
            setShield(shieldClazz);
        }
    }

    private <T, V extends Shield<T>> void setShield(Class<V> shieldClazz) {
        Class<T> objectClazz;
        objectClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(shieldClazz);

        if (objectClazz == null) {
            return;
        }

        ShieldInfo<T> shieldInfo = new ShieldInfo<T>(shieldClazz);

        for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, shieldClazz.getSimpleName())) {
            if (endpoint.getShieldInfo() != null) {
                throwDuplicateShield(shieldClazz, objectClazz, endpoint);
            }

            endpoint.setShieldInfo(shieldInfo);
        }
    }

    private <V extends Shield<T>, T> void throwDuplicateShield(Class<V> shieldClazz, Class<T> objectClazz, EndpointFeatures<? extends T> endpoint) {
        ShieldInfo<?> existingShieldInfo = endpoint.getShieldInfo();

        throw new RuntimeException("Trying to a second shield '" + shieldClazz.getName() + "' for endpoint '"
                + objectClazz.getName() + "'. The shield '" + existingShieldInfo.getShieldClazz().getName()
                + "' was already associated. Endpoints can have only one Shield.");
    }

    private void scanHooks() {
        Map<EndpointFeatures<?>, FeatureTree<Hook>> hookTrees = new HashMap<>();

        Set<Class<? extends Hook>> clazzes = endpointsPackage.getSubTypesOf(Hook.class);

        for (Class<? extends Hook> hookClazz : clazzes) {
            if (Modifier.isAbstract(hookClazz.getModifiers())) {
                continue;
            }
            addHook(hookClazz, hookTrees);
        }
    }

    private <T, V extends Hook<T>> void addHook(Class<V> hookClazz, Map<EndpointFeatures<?>, FeatureTree<Hook>> hookTrees) {
        Class<T> objectClazz = (Class<T>) ReflectionUtils.getFeatureEndpointClazz(hookClazz);

        if (objectClazz == null) {
            return;
        }

        for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, hookClazz.getSimpleName())) {
            //endpoint.addHook(hookClazz);
            endpointTrees.get(endpoint.getClazz()).addHook(hookClazz);
        }
    }

    private void scanTransformers() {
        Set<Class<? extends Transformer>> clazzes = endpointsPackage.getSubTypesOf(Transformer.class);

        for (Class<? extends Transformer> transformerClazz : clazzes) {
            if (Modifier.isAbstract(transformerClazz.getModifiers())) {
                continue;
            }
            Class<?> objectClazz = ReflectionUtils.getFeatureEndpointClazz(transformerClazz);

            if (objectClazz == null) {
                continue;
            }

            addTransformerForObject(objectClazz, transformerClazz);
        }
    }

    private void addTransformerForObject(Class<?> objectClazz, Class<? extends Transformer> transformerClazz) {
        for (Method method : ReflectionUtils.getUniqueMethodsRecursively(transformerClazz, Transformer.class)) {
            for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, transformerClazz.getSimpleName())) {
                endpoint.addTransformer(method.getName(), method);
            }
        }
    }

    private void scanActions() {
        Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

        for (Class<? extends Action> actionClazz : clazzes) {
            if (Modifier.isAbstract(actionClazz.getModifiers())) {
                continue;
            }
            Class<?> objectClazz = ReflectionUtils.getFeatureEndpointClazz(actionClazz);

            if (objectClazz == null) {
                continue;
            }

            addActionMethods(objectClazz, actionClazz);
        }
    }

    private void addActionMethods(Class<?> objectClazz, Class<? extends Action> actionClazz) {

        for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, actionClazz.getSimpleName())) {
            endpointTrees.get(endpoint.getClazz()).addAction(actionClazz);
        }

        // TODO: remove
//        for (Method method : actionClazz.getDeclaredMethods()) {
//            if (!ActionMethod.isAction(method)) {
//                continue;
//            }
//            addAction(objectClazz, method);
//        }
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

    private void addAction(Class<?> objectClazz, Method method) {

        ActionMethod actionMethod = createActionMethod(objectClazz, method);
        List<ActionKey> actionKeys = actionMethod.getActionKeys();

        if (actionKeys.isEmpty()) {
            return;
        }

        for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, method.getDeclaringClass().getSimpleName())) {


            for (ActionKey actionKey : actionKeys) {
                endpoint.addAction(actionKey, method, actionMethod);
            }
        }
    }

    private ActionMethod createActionMethod(Class<?> objectClazz, Method method) {
        try {
            return new ActionMethod(method);
        } catch (InvalidActionMethodException e) {
            throw new RuntimeException("Invalid Action: " + objectClazz.getName() + "." + method.getName(), e);
        }
    }

    private List<ActionKey> parseActionKeys(Class<?> objectClazz, Method method) {
        try {
            return ActionMethod.getActionKeysFor(method);
        } catch (InvalidActionMethodException e) {
            throw new RuntimeException("Invalid Action: " + objectClazz.getName() + "." + method.getName(), e);
        }
    }

    public EndpointScanner enableHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
        return this;
    }

}
