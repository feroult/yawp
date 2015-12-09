package io.yawp.repository.actions;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectModel;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionMethod {

    private Method method;

    private Class<?> endpointClazz;

    private Type[] parameterTypes;

    private final ObjectModel objectModel;

    private Type[] genericParameterTypes;

    private ActionParameters parameters;

    private final List<ActionKey> actionKeys;

    public ActionMethod(Method method) throws InvalidActionMethodException {
        this.parameters = new ActionParameters(method);

        this.method = method;
        this.endpointClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
        this.parameterTypes = method.getParameterTypes();
        this.genericParameterTypes = method.getGenericParameterTypes();
        this.objectModel = new ObjectModel(endpointClazz);
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

    public Object[] createArguments(IdRef<?> id, Map<String, String> params) {
        return parameters.createArguments(id, params);

    }


    private boolean isParamaterTypeOf(int index, Class<?> clazz) {
        return parameterTypes[index].equals(clazz);
    }

    private int parameterCount() {
        return parameterTypes.length;
    }

}
