package io.yawp.repository.scanner;

import io.yawp.repository.actions.Action;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.hooks.Hook;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndpointTree<T> {

    private FeatureTree<Action> actionTree = new FeatureTree<>(Action.class);

    private FeatureTree<Hook> hookTree = new FeatureTree<>(Hook.class);

    public void addAction(Class<? extends Action> actionClazz) {
        actionTree.add(actionClazz);
    }

    public void addHook(Class<? extends Hook> hookClazz) {
        hookTree.add(hookClazz);
    }

    public Map<ActionKey, ActionMethod> loadActions() {
        Map<ActionKey, ActionMethod> map = new HashMap<>();

        for (Class<? extends Action> actionClazz : actionTree.getLeafs()) {
            addActionKeys(map, actionClazz);
        }

        return map;
    }

    private void addActionKeys(Map<ActionKey, ActionMethod> map, Class<? extends Action> actionClazz) {
        for (Method method : actionClazz.getDeclaredMethods()) {
            if (!ActionMethod.isAction(method)) {
                continue;
            }

            ActionMethod actionMethod = createActionMethod(method);
            List<ActionKey> actionKeys = actionMethod.getActionKeys();

            for (ActionKey actionKey : actionKeys) {
                map.put(actionKey, actionMethod);
            }
        }
    }

    private ActionMethod createActionMethod(Method method) {
        try {
            return new ActionMethod(method);
        } catch (InvalidActionMethodException e) {
            throw new RuntimeException("Invalid Action: " + method.getDeclaringClass().getName() + "." + method.getName(), e);
        }
    }

    public Set<Class<? extends Hook>> loadHooks() {
        return hookTree.getLeafs();
    }
}
