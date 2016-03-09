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
        return getFeatureTypeArgumentAt(clazz, 0);
    }

    public static Class<?> getFeatureTypeArgumentAt(Class<?> clazz, int index) {
        Type superClassGenericType = getGenericTypeArgumentAt(clazz.getGenericSuperclass(), index);

        if (superClassGenericType instanceof TypeVariable) {
            return (Class<?>) getGenericTypeBound(clazz, ((TypeVariable) superClassGenericType).getName());
        }

        return (Class<?>) superClassGenericType;
    }

    public static Class<?> getIdRefEndpointClazz(Field field) {
        return (Class<?>) getGenericTypeArgumentAt(field.getGenericType(), 0);
    }

    private static Type[] getGenericTypeArguments(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return new Type[]{};
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getActualTypeArguments();
    }

    private static Type getGenericTypeArgumentAt(Type type, int index) {
        Type[] parameters = getGenericTypeArguments(type);
        if (parameters.length <= index) {
            return null;
        }
        return parameters[index];
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

    public static List<Method> getPublicMethodsRecursively(Class<?> clazz, Class<?> stopClazz) {
        Set<String> uniqueNames = new HashSet<String>();
        List<Method> methods = new ArrayList<>();

        while (!isJavaClass(clazz) && clazz != stopClazz) {
            methods.addAll(ReflectionUtils.getImmediatePublicMethods(clazz));
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    private static List<Method> getImmediatePublicMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            methods.add(method);
        }
        return methods;
    }

    public static Class<?> clazzForName(String clazzName) {
        try {
            return Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getListGenericType(Type type) {
        Type firstArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (!(firstArgument instanceof ParameterizedType)) {
            return (Class<?>) firstArgument;
        }
        return (Class<?>) ((ParameterizedType) firstArgument).getRawType();
    }
}