package io.yawp.repository.tools.scanner;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.actions.Action;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.shields.ShieldInfo;
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

    private FeatureTree<Shield> shieldTree = new FeatureTree<>(Shield.class);

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

    public void addShield(Class<? extends Shield> shieldClazz) {
        shieldTree.add(shieldClazz);
    }

    public Map<ActionKey, ActionMethod> loadActions(Map<Class<?>, Map<ActionKey, ActionMethod>> cache) {
        Map<ActionKey, ActionMethod> map = new HashMap<>();

        for (Class<? extends Action> actionClazz : actionTree.getLeafs()) {
            addActionKeys(map, actionClazz, cache);
        }

        return map;
    }

    public Set<Class<? extends Hook>> loadHooks() {
        return hookTree.getLeafs();
    }

    public Map<String, Method> loadTransformers(Map<Class<?>, Map<String, Method>> cache) {
        Map<String, Method> map = new HashMap<>();

        for (Class<? extends Transformer> transformerClazz : transformerTree.getLeafs()) {
            addTransformerMethods(map, transformerClazz, cache);
        }

        return map;
    }

    public <TT> ShieldInfo<TT> loadShield() {
        Set<Class<? extends Shield>> shieldClazzes = shieldTree.getLeafs();
        if (shieldClazzes.size() == 0) {
            return null;
        }

        if (shieldClazzes.size() > 1) {
            throwExceptionMultipleShields(shieldClazzes);
        }
        return new ShieldInfo<TT>((Class<? extends Shield<? super TT>>) shieldClazzes.iterator().next());
    }

    private void throwExceptionMultipleShields(Set<Class<? extends Shield>> shieldClazzes) {
        throw new RuntimeException(String.format("Trying to add multiple shields for endpoint '%s' -> %s",
                endpointClazz.getName(),
                createShieldsString(shieldClazzes)));
    }

    private String createShieldsString(Set<Class<? extends Shield>> shieldClazzes) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Class<? extends Shield> shieldClazz : shieldClazzes) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(shieldClazz.getName());
        }
        return sb.toString();
    }

    private void addTransformerMethods(Map<String, Method> map, Class<? extends Transformer> transformerClazz, Map<Class<?>, Map<String, Method>> cache) {

        if (cache.containsKey(transformerClazz)) {
            map.putAll(cache.get(transformerClazz));
            return;
        }

        Map<String, Method> addToCache = new HashMap<>();
        cache.put(transformerClazz, addToCache);

        for (Method method : ReflectionUtils.getPublicMethodsRecursively(transformerClazz, Transformer.class)) {
            String name = method.getName();

            if (isTransformerOverriden(name, addToCache)) {
                continue;
            }

            assertTransformerNotDuplicated(map, name, method);
            map.put(name, method);
            addToCache.put(name, method);
        }
    }

    private boolean isTransformerOverriden(String name, Map<String, Method> addToCache) {
        return addToCache.containsKey(name);
    }

    private void assertTransformerNotDuplicated(Map<String, Method> map, String name, Method method) {
        if (map.containsKey(name)) {
            Method existingMethod = map.get(name);

            if (method.equals(existingMethod)) {
                return;
            }

            throw new RuntimeException("Trying to add two transformers with the same name '" + name + "' to "
                    + endpointClazz.getName() + ": one at " + existingMethod.getDeclaringClass().getName() + " and the other at "
                    + method.getDeclaringClass().getName());
        }
    }

    private void addActionKeys(Map<ActionKey, ActionMethod> map, Class<? extends Action> actionClazz, Map<Class<?>, Map<ActionKey, ActionMethod>> cache) {

        if (cache.containsKey(actionClazz)) {
            map.putAll(cache.get(actionClazz));
            return;
        }

        Map<ActionKey, ActionMethod> addToCache = new HashMap<>();
        cache.put(actionClazz, addToCache);

        for (Method method : ReflectionUtils.getPublicMethodsRecursively(actionClazz, Action.class)) {
            //for (Method method : actionClazz.getDeclaredMethods()) {
            if (!ActionMethod.isAction(method)) {
                continue;
            }

            ActionMethod actionMethod = createActionMethod(method);
            List<ActionKey> actionKeys = actionMethod.getActionKeys();

            for (ActionKey actionKey : actionKeys) {
                if (isActionOverriden(actionKey, addToCache)) {
                    continue;
                }

                assertActionNotDuplicated(map, actionKey, method);
                map.put(actionKey, actionMethod);
                addToCache.put(actionKey, actionMethod);
            }
        }
    }

    private boolean isActionOverriden(ActionKey actionKey, Map<ActionKey, ActionMethod> addToCache) {
        return addToCache.containsKey(actionKey);
    }

    private void assertActionNotDuplicated(Map<ActionKey, ActionMethod> map, ActionKey actionKey, Method method) {
        if (map.get(actionKey) != null) {
            Method existingMethod = map.get(actionKey).getMethod();

            if (method.equals(existingMethod)) {
                return;
            }

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
