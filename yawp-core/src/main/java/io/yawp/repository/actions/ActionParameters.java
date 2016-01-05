package io.yawp.repository.actions;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectModel;
import io.yawp.repository.Repository;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionParameters {

    private enum ParameterType {
        ID, PARENT_ID, JSON, PARAMS
    }

    private Method method;

    private Class<?> endpointClazz;

    private List<ParameterType> order = new ArrayList<>();

    private Map<ParameterType, Integer> count = new HashMap<>();

    private Class<?> jsonClazz;

    private Type jsonGenericType;

    public ActionParameters(Method method) throws InvalidActionMethodException {
        this.method = method;
        setEndpointClazz(method);

        init();

        if (!isValid()) {
            throw new InvalidActionMethodException();
        }
    }

    private void setEndpointClazz(Method method) {
        this.endpointClazz = ReflectionUtils.getFeatureEndpointClazz(method.getDeclaringClass());
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

    public Object[] createArguments(Repository r, IdRef<?> id, String json, Map<String, String> params) {
        List<Object> arguments = new ArrayList();

        for (ParameterType type : order) {
            switch (type) {
                case ID:
                case PARENT_ID:
                    arguments.add(id);
                    break;
                case JSON:
                    arguments.add(getJsonArgument(r, json));
                    break;
                case PARAMS:
                    arguments.add(params);
                    break;
            }
        }

        return arguments.toArray();
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
                defineJsonClazz(parameterInfo);
            }
        }
    }

    private void defineJsonClazz(ParameterInfo parameterInfo) {
        jsonClazz = parameterInfo.getParameterClazz();
        if (jsonClazz.equals(List.class)) {
            jsonGenericType = parameterInfo.getGenericTypeAt(0);
        }
    }

    private Object getJsonArgument(Repository r, String json) {
        if (jsonClazz.equals(String.class)) {
            return json;
        }
        if (jsonClazz.equals(List.class)) {
            return JsonUtils.fromList(r, json, (Class<?>) jsonGenericType);
        }
        return JsonUtils.from(r, json, jsonClazz);
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
            Type genericType = getGenericTypeAt(0);
            if (genericType instanceof WildcardType) {
                return true;
            }
            return genericType.equals(endpointClazz);
        }

        public boolean isParentId() {
            if (!isTypeOf(IdRef.class)) {
                return false;
            }
            ObjectModel objectModel = new ObjectModel(endpointClazz);
            Type genericType = getGenericTypeAt(0);
            if (genericType instanceof WildcardType) {
                return false;
            }
            return objectModel.isAncestor((Class<?>) genericType);
        }

        public boolean isParams() {
            if (!isTypeOf(Map.class)) {
                return false;
            }

            Type keyClazz = getGenericTypeAt(0);
            Type valueClazz = getGenericTypeAt(1);

            return keyClazz.equals(String.class) && valueClazz.equals(String.class);
        }

        public Class<?> getParameterClazz() {
            return parameter;
        }

        public Type getGenericTypeAt(int index) {
            return ((ParameterizedType) parameterGenerics).getActualTypeArguments()[index];
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
    }
}
