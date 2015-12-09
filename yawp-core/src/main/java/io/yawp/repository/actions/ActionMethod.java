package io.yawp.repository.actions;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.ThrownExceptionsUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionMethod {

    private Method method;

    private ActionParameters parameters;

    private final List<ActionKey> actionKeys;

    public ActionMethod(Method method) throws InvalidActionMethodException {
        this.method = method;
        this.parameters = new ActionParameters(method);
        this.actionKeys = parseActionKeys();
    }

    public List<ActionKey> getActionKeys() {
        return actionKeys;
    }

    public static boolean isAction(Method method) {
        for (HttpVerb verb : HttpVerb.values()) {
            if (verb.hasAnnotation(method)) {
                return true;
            }
        }
        return false;
    }

    public static List<ActionKey> getActionKeysFor(Method method) throws InvalidActionMethodException {
        ActionMethod actionMethod = new ActionMethod(method);
        return actionMethod.getActionKeys();
    }

    public Method getMethod() {
        return method;
    }

    private List<ActionKey> parseActionKeys() {
        List<ActionKey> actionKeys = new ArrayList<>();

        for (HttpVerb verb : HttpVerb.values()) {
            if (!hasAnnotationFor(verb)) {
                continue;
            }

            String value = verb.getAnnotationValue(method);
            actionKeys.add(new ActionKey(verb, value, parameters.isOverCollection()));
        }
        return actionKeys;
    }

    private boolean hasAnnotationFor(HttpVerb verb) {
        return verb.hasAnnotation(method);
    }

    public Object[] createArguments(IdRef<?> id, String json, Map<String, String> params) {
        return parameters.createArguments(id, json, params);
    }

    public boolean isAtomicCrossEntities() {
        return method.getAnnotation(Atomic.class).cross();
    }

    public boolean isAtomic() {
        return method.isAnnotationPresent(Atomic.class);
    }

    public Object invoke(Repository r, IdRef<?> id, String json, Map<String, String> params) {
        try {
            Class<? extends Action<?>> actionClazz = (Class<? extends Action<?>>) method.getDeclaringClass();
            Action<?> actionInstance = actionClazz.newInstance();
            actionInstance.setRepository(r);
            return method.invoke(actionInstance, createArguments(id, json, params));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw ThrownExceptionsUtils.handle(e);
        }
    }
}
