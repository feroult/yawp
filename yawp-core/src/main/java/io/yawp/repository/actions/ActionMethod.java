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

    public ActionMethod(Method method) {
        this.method = method;
    }

    public boolean hasAnnotationFor(HttpVerb verb) {
        return verb.hasAnnotation(method);
    }

    public static List<ActionKey> getActionKeysFor(Method method) throws InvalidActionMethodException {
        ActionMethod actionMethod = new ActionMethod(method);
        return actionMethod.getActionKeys();
    }

    public List<ActionKey> getActionKeys() throws InvalidActionMethodException {
        List<ActionKey> actionKeys = new ArrayList<>();

        for (HttpVerb verb : HttpVerb.values()) {
            if (!hasAnnotationFor(verb)) {
                continue;
            }

            if (!isValid()) {
                throw new InvalidActionMethodException();
            }

            String value = verb.getAnnotationValue(method);
            actionKeys.add(new ActionKey(verb, value, isOverCollection()));
        }
        return actionKeys;
    }

    public Object[] createArguments(IdRef<?> id, Map<String, String> params) {
        if (method.getParameterTypes().length == 0) {
            return new Object[]{};
        }
        if (method.getParameterTypes().length == 2) {
            return new Object[]{id, params};
        }

        if (IdRef.class.equals(method.getParameterTypes()[0])) {
            return new Object[]{id};
        }

        return new Object[]{params};
    }

    private boolean isValid() {

        if (rootCollection(method)) {
            return true;
        }

        if (singleObject(method)) {
            return true;
        }

        if (parentCollection(method)) {
            return true;
        }

        return false;
    }

    private boolean isOverCollection() {
        Type[] parameters = method.getGenericParameterTypes();

        if (parameters.length == 0) {
            return true;
        }

        if (parameters[0].equals(Map.class)) {
            return true;
        }

        Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
        ParameterizedType pType = (ParameterizedType) parameters[0];
        return !pType.getActualTypeArguments()[0].equals(objectClazz);
    }


    private boolean parentCollection(Method method) {
        Type[] genericTypes = method.getGenericParameterTypes();
        Type[] types = method.getParameterTypes();

        ObjectModel model = getObjectModel(method);
        Class<?> parentClazz = model.getParentClazz();

        if (types.length == 1 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(parentClazz)) {
            return true;
        }

        if (types.length == 2 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(parentClazz)
                && types[1].equals(Map.class)) {
            return true;
        }

        return false;
    }

    private ObjectModel getObjectModel(Method method) {
        Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
        ObjectModel model = new ObjectModel(objectClazz);
        return model;
    }

    private boolean singleObject(Method method) {
        Type[] genericTypes = method.getGenericParameterTypes();
        Type[] types = method.getParameterTypes();

        Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());

        if (types.length == 1 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(objectClazz)) {
            return true;
        }

        if (types.length == 2 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(objectClazz)
                && types[1].equals(Map.class)) {
            return true;
        }

        return false;
    }

    private boolean rootCollection(Method method) {
        Type[] types = method.getParameterTypes();

        if (types.length == 0) {
            return true;
        }

        if (types.length == 1 && types[0].equals(Map.class)) {
            return true;
        }

        return false;
    }

    private Type getParameterType(Type[] parameters, int index) {
        return ((ParameterizedType) parameters[index]).getActualTypeArguments()[index];
    }

}
