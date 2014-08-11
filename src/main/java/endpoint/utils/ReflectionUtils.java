package endpoint.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {
        throw new RuntimeException("Should not be instanciated");
    }

    public static boolean isInnerClass(Class<?> clazz) {
        return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers());
    }

    public static List<Field> getImmediateFields(Class<?> clazz) {
    	List<Field> finalFields = new ArrayList<>();
    	for (Field field : clazz.getDeclaredFields()) {
    		if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
    			finalFields.add(field);
    		}
    	}
        return finalFields;
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
}