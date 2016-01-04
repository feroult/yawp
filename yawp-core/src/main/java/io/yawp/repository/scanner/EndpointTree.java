package io.yawp.repository.scanner;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.actions.Action;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.transformers.Transformer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndpointTree<T> {

    private Class<?> endpointClazz;

    private FeatureTree<Action> actionTree = new FeatureTree<>(Action.class);

    private FeatureTree<Hook> hookTree = new FeatureTree<>(Hook.class);

    private FeatureTree<Transformer> transformerTree = new FeatureTree<>(Transformer.class);

    public EndpointTree(Class<T> endpointClazz) {
        this.endpointClazz = endpointClazz;
    }

    public void addAction(Class<? extends Action> actionClazz) {
        actionTree.add(actionClazz);
    }

    public void addHook(Class<? extends Hook> hookClazz) {
        hookTree.add(hookClazz);
    }

    public void addTransformer(Class<? extends Transformer> transformerClazz) {
        transformerTree.add(transformerClazz);
    }

    public Map<ActionKey, ActionMethod> loadActions() {
        Map<ActionKey, ActionMethod> map = new HashMap<>();

        for (Class<? extends Action> actionClazz : actionTree.getLeafs()) {
            addActionKeys(map, actionClazz);
        }

        return map;
    }

    public Set<Class<? extends Hook>> loadHooks() {
        return hookTree.getLeafs();
    }

    public Map<String, Method> loadTransformers() {
        Map<String, Method> map = new HashMap<>();

        for (Class<? extends Transformer> transformerClazz : transformerTree.getLeafs()) {
            addTransformerMethods(map, transformerClazz);
        }

        return map;
    }

    private void addTransformerMethods(Map<String, Method> map, Class<? extends Transformer> transformerClazz) {
        for (Method method : ReflectionUtils.getUniqueMethodsRecursively(transformerClazz, Transformer.class)) {
            String name = method.getName();
            if (!checkTransformerDuplication(map, name, method)) {
                continue;
            }
            map.put(name, method);
        }
    }

    private boolean checkTransformerDuplication(Map<String, Method> map, String name, Method method) {
        Method existingMethod = map.get(name);
        if (existingMethod != null) {
            if (!method.equals(existingMethod)) {
                throw new RuntimeException("Trying to add two transformers with the same name '" + name + "' to "
                        + endpointClazz.getName() + ": one at " + existingMethod.getDeclaringClass().getName() + " and the other at "
                        + method.getDeclaringClass().getName());
            }
            return false;
        }
        return true;
    }

    private void addActionKeys(Map<ActionKey, ActionMethod> map, Class<? extends Action> actionClazz) {
        for (Method method : actionClazz.getDeclaredMethods()) {
            if (!ActionMethod.isAction(method)) {
                continue;
            }

            ActionMethod actionMethod = createActionMethod(method);
            List<ActionKey> actionKeys = actionMethod.getActionKeys();

            for (ActionKey actionKey : actionKeys) {
                assertActionNotDuplicated(map, actionKey, method);
                map.put(actionKey, actionMethod);
            }
        }
    }

    private void assertActionNotDuplicated(Map<ActionKey, ActionMethod> map, ActionKey actionKey, Method method) {
        if (map.get(actionKey) != null) {
            Method existingMethod = map.get(actionKey).getMethod();
            throw new RuntimeException("Trying to add two actions with the same name '" + actionKey + "' to "
                    + endpointClazz.getName() + ": one at " + existingMethod.getDeclaringClass().getName() + " and the other at "
                    + method.getDeclaringClass().getName());
        }
    }

    private ActionMethod createActionMethod(Method method) {
        try {
            return new ActionMethod(method);
        } catch (InvalidActionMethodException e) {
            throw new RuntimeException("Invalid Action: " + method.getDeclaringClass().getName() + "." + method.getName(), e);
        }
    }
}
