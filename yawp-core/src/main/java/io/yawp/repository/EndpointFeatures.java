package io.yawp.repository;

import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.ShieldInfo;

import java.lang.reflect.Method;
import java.util.*;

public class EndpointFeatures<T> {

    private Class<T> clazz;

    private Map<ActionKey, ActionMethod> actions;

    private Map<String, Method> transformers;

    private Set<Class<? extends Hook>> hooks;

    private ShieldInfo<? super T> shieldInfo;

    public EndpointFeatures(Class<T> clazz) {
        this.clazz = clazz;
        this.actions = new HashMap<>();
        this.transformers = new HashMap<>();
        this.hooks = new HashSet<>();
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public void addAction(ActionKey actionKey, Method method, ActionMethod actionMethod) {
        assertActionNotDuplicated(actionKey, method);
        actions.put(actionKey, actionMethod);
    }

    public void addTransformer(String name, Method method) {
        if (!checkTransformerDuplication(name, method)) {
            return;
        }
        transformers.put(name, method);
    }

    public void addHook(Class<? extends Hook<? super T>> hook) {
        hooks.add(hook);
    }

    public Set<Class<? extends Hook>> getHooks() {
        return hooks;
    }

    public ActionMethod getAction(ActionKey key) {
        return actions.get(key);
    }

    public Class<?> getActionClazz(ActionKey key) {
        return actions.get(key).getMethod().getDeclaringClass();
    }

    public Method getTransformer(String name) {
        return transformers.get(name);
    }

    public Endpoint getEndpointAnnotation() {
        return clazz.getAnnotation(Endpoint.class);
    }

    public String getEndpointPath() {
        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        if (endpoint == null) {
            throw new RuntimeException("The class " + clazz + " was used as an entity but was not annotated with @Endpoint.");
        }
        return endpoint.path();
    }

    public boolean hasCustomAction(ActionKey actionKey) {
        return actions.containsKey(actionKey);
    }

    public boolean hasTranformer(String transformerName) {
        return transformers.containsKey(transformerName);
    }

    public boolean hasShield() {
        return shieldInfo != null;
    }

    public ShieldInfo<? super T> getShieldInfo() {
        return shieldInfo;
    }

    private boolean checkTransformerDuplication(String key, Method method) {
        Method existingMethod = transformers.get(key);
        if (existingMethod != null) {
            if (!method.equals(existingMethod)) {
                throw new RuntimeException("Trying to add two transformers with the same name '" + key + "' to "
                        + clazz.getName() + ": one at " + existingMethod.getDeclaringClass().getName() + " and the other at "
                        + method.getDeclaringClass().getName());
            }
            return false;
        }
        return true;
    }

    private void assertActionNotDuplicated(ActionKey key, Method method) {
        if (actions.get(key) != null) {
            Method existingMethod = actions.get(key).getMethod();
            throw new RuntimeException("Trying to add two actions with the same name '" + key + "' to "
                    + clazz.getName() + ": one at " + existingMethod.getDeclaringClass().getName() + " and the other at "
                    + method.getDeclaringClass().getName());
        }
    }

    public void setActions(Map<ActionKey, ActionMethod> actions) {
        this.actions = actions;
    }

    public void setHooks(Set<Class<? extends Hook>> hooks) {
        this.hooks = hooks;
    }

    public void setTransformers(Map<String, Method> transformers) {
        this.transformers = transformers;
    }


    public void setShieldInfo(ShieldInfo<? super T> shieldInfo) {
        this.shieldInfo = shieldInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndpointFeatures<?> that = (EndpointFeatures<?>) o;

        return clazz.equals(that.clazz);

    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

}
