package io.yawp.repository.shields;

import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShieldInfo<T> {

    private Class<? extends Shield<? super T>> shieldClazz;

    private Map<ActionKey, Method> actionMethods;

    private transient Constructor<? extends Shield<? super T>> constructor;

    public ShieldInfo(Class<? extends Shield<? super T>> shieldClazz) {
        this.shieldClazz = shieldClazz;
        this.constructor = cacheConstructor(shieldClazz);
        parseActionMethods();
    }

    public Class<? extends Shield<? super T>> getShieldClazz() {
        return shieldClazz;
    }

    public Map<ActionKey, Method> getActionMethods() {
        return actionMethods;
    }

    private void parseActionMethods() {
        this.actionMethods = new HashMap<ActionKey, Method>();

        Method[] methods = shieldClazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!ActionMethod.isAction(method)) {
                continue;
            }

            List<ActionKey> actionKeys = getActionKeysFor(method);

            for (ActionKey actionKey : actionKeys) {
                actionMethods.put(actionKey, method);
            }
        }
    }

    private List<ActionKey> getActionKeysFor(Method method) {
        try {
            return ActionMethod.getActionKeysFor(method);
        } catch (InvalidActionMethodException e) {
            throw new RuntimeException("Invalid action method in shield: " + shieldClazz.getName() + "." + method.getName(), e);
        }
    }

    private Constructor<? extends Shield<? super T>> cacheConstructor(Class<? extends Shield<? super T>> shieldClazz) {
        try {
            Constructor<? extends Shield<? super T>> constructor = shieldClazz.getConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Shield<?> newInstance() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
