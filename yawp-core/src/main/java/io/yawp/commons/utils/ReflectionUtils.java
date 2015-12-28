package io.yawp.commons.utils;

import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public final class ReflectionUtils {

    public static List<Field> getImmediateFields(Class<?> clazz) {
        List<Field> finalFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
                finalFields.add(field);
            }
        }
        return finalFields;
    }

    public static Object getFieldValue(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field = getFieldRecursively(clazz, fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(object);
            field.setAccessible(accessible);
            return value;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getFieldRecursively(Class<?> clazz, String fieldName) {
        Class<?> baseClazz = clazz;
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("Field '" + fieldName + "'not found in entity " + baseClazz, new NoSuchFieldException(fieldName));
    }

    public static List<Field> getFieldsRecursively(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (!isJavaClass(clazz)) {
            fields.addAll(ReflectionUtils.getImmediateFields(clazz));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static boolean isBaseClass(Class<?> clazz) {
        return Object.class.equals(clazz) || clazz.isPrimitive() || clazz.isEnum() || clazz.isArray();
    }

    public static boolean isJavaClass(Class<?> clazz) {
        return isBaseClass(clazz) || clazz.getPackage().getName().startsWith("java.") || clazz.getPackage().getName().startsWith("javax.");
    }

    public static Class<?> getEndpointTypeFor(Class<?> clazz) throws ParameterizedEndpointException {
        Class<?>[] parameters = getGenericParameters(clazz);
        if (parameters.length == 0) {
            return null;
        }
        return parameters[0];
    }

    public static Class<?> getEndpointTypeFor(Field field) throws ParameterizedEndpointException {
        Class<?>[] parameters = getGenericParameters(field);
        if (parameters.length == 0) {
            return null;
        }
        return parameters[0];
    }

    private static Class<?>[] getGenericParameters(Class<?> clazz) throws ParameterizedEndpointException {
        return getGenericParametersInternal(clazz.getGenericSuperclass());
    }

    private static Class<?>[] getGenericParameters(Field field) throws ParameterizedEndpointException {
        return getGenericParametersInternal(field.getGenericType());
    }

    private static Class<?>[] getGenericParametersInternal(Type genericFieldType) throws ParameterizedEndpointException {
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            Class<?>[] clazzes = new Class<?>[fieldArgTypes.length];
            for (int i = 0; i < clazzes.length; i++) {

                if (fieldArgTypes[i] instanceof TypeVariableImpl) {
                    throw new ParameterizedEndpointException();
                }

                clazzes[i] = (Class<?>) fieldArgTypes[i];
            }
            return clazzes;
        }
        return new Class<?>[]{};
    }

    public static Field getFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Field theField = null;
        for (Field field : ReflectionUtils.getFieldsRecursively(clazz)) {
            if (field.isAnnotationPresent(annotationClass)) {
                if (theField != null) {
                    throw new RuntimeException("You can have at most one field annotated with the " + annotationClass.getSimpleName()
                            + " class.");
                }
                theField = field;
                theField.setAccessible(true);
            }
        }

        return theField;
    }

    public static Object getter(Object o, String property) {
        try {
            if (Map.class.isInstance(o)) {
                return ((Map<?, ?>) o).get(property);
            }

            return new PropertyDescriptor(property, o.getClass()).getReadMethod().invoke(o);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Method> getUniqueMethodsRecursively(Class<?> clazz, Class<?> stopClazz) {
        Set<String> uniqueNames = new HashSet<String>();
        List<Method> methods = new ArrayList<>();


        while (!isJavaClass(clazz) && clazz != stopClazz) {
            methods.addAll(ReflectionUtils.getImmediateUniquePublicMethods(clazz, uniqueNames));
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    private static List<Method> getImmediateUniquePublicMethods(Class<?> clazz, Set<String> uniqueNames) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (uniqueNames.contains(method.getName())) {
                continue;
            }
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            methods.add(method);
            uniqueNames.add(method.getName());
        }
        return methods;
    }
}