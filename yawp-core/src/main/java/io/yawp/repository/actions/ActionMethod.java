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
            actionKeys.add(new ActionKey(verb, value, isOverCollection()));
        }
        return actionKeys;
    }

    private boolean hasAnnotationFor(HttpVerb verb) {
        return verb.hasAnnotation(method);
    }

    public Object[] createArguments(IdRef<?> id, Map<String, String> params) {
        if (parameterCount() == 0) {
            return new Object[]{};
        }
        if (parameterCount() == 2) {
            return new Object[]{id, params};
        }

        if (isParamaterTypeOf(0, IdRef.class)) {
            return new Object[]{id};
        }

        return new Object[]{params};
    }


    private boolean isOverCollection() {
        if (parameterCount() == 0) {
            return true;
        }

        if (isParamaterTypeOf(0, Map.class)) {
            return true;
        }

        if (!isParamaterTypeOf(0, IdRef.class)) {
            return false;
        }

        return objectModel.isAncestor(getParameterGenericType(0, 0));
    }

    private Class<?> getParameterGenericType(int parameterIndex, int parameterGenericTypeIndex) {
        ParameterizedType parameterGenericTypes = (ParameterizedType) genericParameterTypes[parameterIndex];
        return (Class<?>) parameterGenericTypes.getActualTypeArguments()[parameterGenericTypeIndex];
    }


    private ObjectModel getObjectModel(Method method) {
        Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
        ObjectModel model = new ObjectModel(objectClazz);
        return model;
    }


    private boolean isParamaterTypeOf(int index, Class<?> clazz) {
        return parameterTypes[index].equals(clazz);
    }

    private int parameterCount() {
        return parameterTypes.length;
    }

    private boolean isParameterIdRefOfType(int index, Class<?> clazz) {
        Type[] genericTypes = method.getGenericParameterTypes();
        return isParamaterTypeOf(index, IdRef.class) && getParameterType(genericTypes, index).equals(clazz);
    }

    private Type getParameterType(Type[] parameters, int index) {
        return ((ParameterizedType) parameters[index]).getActualTypeArguments()[index];
    }

}
