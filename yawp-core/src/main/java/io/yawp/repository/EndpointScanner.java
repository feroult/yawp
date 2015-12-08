package io.yawp.repository;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.actions.Action;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.shields.ShieldInfo;
import io.yawp.repository.transformers.Transformer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.reflections.Reflections;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class EndpointScanner {

    private final static Logger LOGGER = Logger.getLogger(EndpointScanner.class.getName());

    private boolean enableHooks;

    private Reflections endpointsPackage;

    private Map<Class<?>, EndpointFeatures<?>> endpoints;

    public EndpointScanner(String packagePrefix) {
        endpointsPackage = new Reflections(packagePrefix);
        endpoints = new HashMap<>();
        enableHooks = true;
    }

    public RepositoryFeatures scan() {
        long start = System.currentTimeMillis();
        RepositoryFeatures repositoryFeatures = new RepositoryFeatures(generateEndpointsMap());
        long elapsed = System.currentTimeMillis() - start;
        LOGGER.info("Yawp! started in " + elapsed + " ms");
        return repositoryFeatures;
    }

    private void scanEndpoints() {
        Set<Class<?>> clazzes = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);

        for (Class<?> endpointClazz : clazzes) {
            endpoints.put(endpointClazz, new EndpointFeatures<>(endpointClazz));
        }
    }

    private Collection<EndpointFeatures<?>> generateEndpointsMap() {
        scanEndpoints();
        scanActions();
        scanTransformers();
        if (enableHooks) {
            scanHooks();
        }
        scanShields();
        return endpoints.values();
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
        Class<T> objectClazz = getShieldObject(shieldClazz);

        ShieldInfo<T> shieldInfo = new ShieldInfo<T>(shieldClazz);

        for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, shieldClazz.getSimpleName())) {
            if (endpoint.getShieldInfo() != null) {
                throwDuplicateShield(shieldClazz, objectClazz, endpoint);
            }

            endpoint.setShield(shieldClazz);
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
        Set<Class<? extends Hook>> clazzes = endpointsPackage.getSubTypesOf(Hook.class);

        for (Class<? extends Hook> hookClazz : clazzes) {
            addHook(hookClazz);
        }
    }

    private <T, V extends Hook<T>> void addHook(Class<V> hookClazz) {
        Class<T> objectClazz = getHookObject(hookClazz);
        for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, hookClazz.getSimpleName())) {
            endpoint.addHook(hookClazz);
        }
    }

    private void scanTransformers() {
        Set<Class<? extends Transformer>> clazzes = endpointsPackage.getSubTypesOf(Transformer.class);

        for (Class<? extends Transformer> transformerClazz : clazzes) {
            Class<?> objectClazz = ReflectionUtils.getGenericParameter(transformerClazz);
            addTransformerForObject(objectClazz, transformerClazz);
        }
    }

    private void addTransformerForObject(Class<?> objectClazz, Class<? extends Transformer> transformerClazz) {
        for (Method method : transformerClazz.getDeclaredMethods()) {
            if (!isValidTransformerMethod(method)) {
                continue;
            }

            for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, transformerClazz.getSimpleName())) {
                endpoint.addTransformer(method.getName(), method);
            }
        }
    }

    private boolean isValidTransformerMethod(Method method) {
        return !method.isSynthetic();
    }

    private void scanActions() {
        Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

        for (Class<? extends Action> actionClazz : clazzes) {
            Class<?> objectClazz = ReflectionUtils.getGenericParameter(actionClazz);

            if (objectClazz == null) {
                continue;
            }

            for (Method method : actionClazz.getDeclaredMethods()) {
                addAction(objectClazz, method);
            }
        }
    }

    // TODO should we think that an objectClazz has more than one endpoint?
    private <T> List<EndpointFeatures<? extends T>> getEndpoints(Class<T> objectClazz, String featureClazz) {
        List<EndpointFeatures<? extends T>> list = new ArrayList<>();
        for (Class<?> endpoint : endpoints.keySet()) {
            if (objectClazz.isAssignableFrom(endpoint)) {
                list.add((EndpointFeatures<T>) endpoints.get(endpoint));
            }
        }
        if (list.isEmpty()) {
            throw new RuntimeException("Tryed to create feature '" + featureClazz + "' with entity '" + objectClazz.getSimpleName()
                    + "' that is not an @Endpoint nor do any io.yawp inherits from it.");
        }
        return list;
    }

    private void addAction(Class<?> objectClazz, Method method) {

        List<ActionKey> actionKeys = parseActionKeys(objectClazz, method);

        if (actionKeys.isEmpty()) {
            return;
        }

        for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, method.getDeclaringClass().getSimpleName())) {
            for (ActionKey actionKey : actionKeys) {
                endpoint.addAction(actionKey, method);
            }
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

    private static <T> Class<T> getHookObject(Class<? extends Hook<T>> hook) {
        return (Class<T>) ReflectionUtils.getGenericParameter(hook);
    }

    private static <T> Class<T> getShieldObject(Class<? extends Shield<T>> hook) {
        return (Class<T>) ReflectionUtils.getGenericParameter(hook);
    }

}
