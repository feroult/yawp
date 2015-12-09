package io.yawp.repository.actions;

import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectModel;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionParameters {

    private enum ParameterType {
        ID, PARENT_ID, PARAMS, JSON
    }

    private Method method;

    private Class<?> endpointClazz;

    private final ObjectModel objectModel;

    private List<ParameterType> order = new ArrayList<>();

    private Map<ParameterType, Integer> count = new HashMap<>();

    private Class<?> jsonClazz;

    private Type jsonGenericType;

    public ActionParameters(Method method) throws InvalidActionMethodException {
        this.method = method;
        this.endpointClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
        this.objectModel = new ObjectModel(endpointClazz);

        init();

        if (!isValid()) {
            throw new InvalidActionMethodException();
        }
    }

    public int size() {
        return order.size();
    }

    public boolean isValid() {
        return isRootCollection() || isSingleObject() || isParentCollection();
    }

    public boolean isOverCollection() {
        if (size() == 0) {
            return true;
        }

        if (size() == 1) {
            return count(ParameterType.PARENT_ID) == 1 || count(ParameterType.JSON) == 1 || count(ParameterType.PARAMS) == 1;
        }

        if (size() == 2) {
            if (count(ParameterType.PARENT_ID) == 1) {
                return count(ParameterType.JSON) == 1 || count(ParameterType.PARAMS) == 1;
            }

            if (count(ParameterType.JSON) == 1) {
                return count(ParameterType.PARENT_ID) == 1 || count(ParameterType.PARAMS) == 1;
            }

            if (count(ParameterType.PARAMS) == 1) {
                return count(ParameterType.PARENT_ID) == 1 || count(ParameterType.JSON) == 1;
            }
        }

        if (size() == 3) {
            return count(ParameterType.PARENT_ID) == 1 && count(ParameterType.PARAMS) == 1 && count(ParameterType.JSON) == 1;
        }

        return false;
    }

    public Object[] createArguments(IdRef<?> id, Map<String, String> params) {
        if (size() == 0) {
            return new Object[]{};
        }

        if (size() == 1) {
            if (count(ParameterType.ID) == 1 || count(ParameterType.PARENT_ID) == 1) {
                return new Object[]{id};
            }
            return new Object[]{params};
        }

        if (size() == 2) {
            return new Object[]{id, params};
        }

        return null;
    }

    private boolean isRootCollection() {
        if (size() == 0) {
            return true;
        }

        if (size() == 1) {
            return count(ParameterType.JSON) == 1 || count(ParameterType.PARAMS) == 1;
        }

        if (size() == 2) {
            return count(ParameterType.JSON) == 1 && count(ParameterType.PARAMS) == 1;
        }

        return false;
    }

    private boolean isSingleObject() {
        if (size() == 1) {
            return count(ParameterType.ID) == 1;
        }

        if (size() == 2) {
            return count(ParameterType.ID) == 1 && (count(ParameterType.JSON) == 1 || count(ParameterType.PARAMS) == 1);
        }

        if (size() == 3) {
            return count(ParameterType.ID) == 1 && count(ParameterType.JSON) == 1 && count(ParameterType.PARAMS) == 1;
        }

        return false;
    }

    private boolean isParentCollection() {
        if (size() == 1) {
            return count(ParameterType.PARENT_ID) == 1;
        }

        if (size() == 2) {
            return count(ParameterType.PARENT_ID) == 1 && (count(ParameterType.JSON) == 1 || count(ParameterType.PARAMS) == 1);
        }

        if (size() == 3) {
            return count(ParameterType.PARENT_ID) == 1 && count(ParameterType.JSON) == 1 && count(ParameterType.PARAMS) == 1;
        }

        return false;

    }

    private void init() throws InvalidActionMethodException {
        Class<?>[] parameters = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        for (int i = 0; i < parameters.length; i++) {
            ParameterInfo parameterInfo = new ParameterInfo(parameters[i], genericParameterTypes[i]);
            ParameterType type = parameterInfo.getType();

            order.add(type);
            incrementCount(type);

            if (type == ParameterType.JSON) {
                jsonClazz = parameters[i];
                jsonGenericType = genericParameterTypes[i];
            }
        }
    }

    private void incrementCount(ParameterType type) {
        if (count.containsKey(type)) {
            count.put(type, count.get(type) + 1);
        } else {
            count.put(type, 1);
        }
    }

    private int count(ParameterType type) {
        if (!count.containsKey(type)) {
            return 0;
        }
        return count.get(type);
    }

    private class ParameterInfo {

        private final Class<?> parameter;

        private final Type parameterGenerics;

        public ParameterInfo(Class<?> parameter, Type parameterGenerics) {
            this.parameter = parameter;
            this.parameterGenerics = parameterGenerics;
        }

        public ParameterType getType() throws InvalidActionMethodException {
            if (isId()) {
                return ParameterType.ID;
            }

            if (isParentId()) {
                return ParameterType.PARENT_ID;
            }

            if (isParams()) {
                return ParameterType.PARAMS;
            }

            if (isJson()) {
                return ParameterType.JSON;
            }

            throw new InvalidActionMethodException();
        }

        private boolean isId() {
            if (!isTypeOf(IdRef.class)) {
                return false;
            }
            return getGenericTypeAt(0).equals(endpointClazz);
        }

        public boolean isParentId() {
            if (!isTypeOf(IdRef.class)) {
                return false;
            }
            // TODO fix w/ ancestor
            return getGenericTypeAt(0).equals(objectModel.getParentClazz());
        }

        public boolean isParams() {
            if (!isTypeOf(Map.class)) {
                return false;
            }

            Type keyClazz = getGenericTypeAt(0);
            Type valueClazz = getGenericTypeAt(1);

            return keyClazz.equals(String.class) && valueClazz.equals(String.class);
        }

        private boolean isJson() {
            if (isTypeOf(IdRef.class)) {
                return false;
            }

            if (isTypeOf(Map.class)) {
                return false;
            }

            return true;
        }

        private boolean isTypeOf(Class<?> clazz) {
            return parameter.equals(clazz);
        }

        private Type getGenericTypeAt(int index) {
            return ((ParameterizedType) parameterGenerics).getActualTypeArguments()[index];
        }
    }
}
