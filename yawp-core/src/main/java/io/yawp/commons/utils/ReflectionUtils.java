package io.yawp.commons.utils;


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

    public static Class<?> getFeatureEndpointClazz(Class<?> clazz) {
        Type superClassGenericType = getFirstGenericTypeArgument(clazz.getGenericSuperclass());

        if (superClassGenericType instanceof TypeVariable) {
            return (Class<?>) getGenericTypeBound(clazz, ((TypeVariable) superClassGenericType).getName());
        }

        return (Class<?>) superClassGenericType;
    }

    public static Class<?> getIdRefEndpointClazz(Field field) {
        return (Class<?>) getFirstGenericTypeArgument(field.getGenericType());
    }

    private static Type getFirstGenericTypeArgument(Type type) {
        Type[] parameters = getGenericTypeArguments(type);
        if (parameters.length == 0) {
            return null;
        }
        return parameters[0];
    }

    private static Type[] getGenericTypeArguments(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getActualTypeArguments();
    }

    private static Type getGenericTypeBound(Class<?> clazz, String name) {
        for (Type type : clazz.getTypeParameters()) {
            if (!(type instanceof TypeVariable)) {
                continue;
            }

            TypeVariable genericType = (TypeVariable) type;

            if (genericType.getName().equals(name)) {
                if (genericType.getBounds().length > 0) {
                    return genericType.getBounds()[0];
                }
                return null;
            }
        }

        return null;
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