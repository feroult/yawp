package io.yawp.commons.http;

import io.yawp.commons.http.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum HttpVerb {

    GET(GET.class),
    POST(POST.class),
    PUT(PUT.class),
    PATCH(PATCH.class),
    DELETE(DELETE.class),
    OPTIONS;

    private Class<? extends Annotation> annotationClazz;

    HttpVerb() {
    }

    HttpVerb(Class<? extends Annotation> annotation) {
        this.annotationClazz = annotation;

    }

    public static HttpVerb fromString(String method) {
        String methodLowerCase = method.toUpperCase();
        return valueOf(methodLowerCase);
    }

    public boolean hasAnnotation(Method method) {
        if (annotationClazz == null) {
            return false;
        }
        return method.isAnnotationPresent(annotationClazz);
    }

    public String getAnnotationValue(Method method) {
        try {
            Annotation annotation = method.getAnnotation(annotationClazz);
            Method valueMethod = annotation.getClass().getMethod("value");
            String value = (String) valueMethod.invoke(annotation);

            if (StringUtils.isEmpty(value)) {
                return useMethodNameAsActionPath(method);
            }

            return value;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String useMethodNameAsActionPath(Method method) {
        return method.getName().replaceAll("(.)(\\p{Lu})", "$1-$2").toLowerCase();
    }
}
