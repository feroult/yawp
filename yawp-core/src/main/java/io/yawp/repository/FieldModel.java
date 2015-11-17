package io.yawp.repository;

import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

public class FieldModel {

    private Field field;

    public FieldModel(Field field) {
        this.field = field;
        field.setAccessible(true);
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Object getValue(Object object) {
        try {
            return field.get(object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isId() {
        return field.isAnnotationPresent(Id.class);
    }

    public boolean hasIndex() {
        return field.getAnnotation(Index.class) != null;
    }

    public boolean isIndexNormalizable() {
        if (!hasIndex()) {
            throw new RuntimeException("You must add @Index annotation the the field '" + field.getName()
                    + "' if you want to use it as a index in where statements.");
        }
        return getIndex().normalize() && isString();
    }

    public boolean isEnum(Object value) {
        return value != null && value.getClass().isEnum();
    }

    public boolean isCollection(Object value) {
        return Collection.class.isInstance(value);
    }

    public boolean isEnum() {
        return field.getType().isEnum();
    }

    public boolean isIdRef() {
        return IdRef.class.isAssignableFrom(field.getType());
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(field.getType());
    }

    public boolean isSaveAsJson() {
        return field.getAnnotation(Json.class) != null;
    }

    public boolean isSaveAsText() {
        return field.getAnnotation(io.yawp.repository.annotations.Text.class) != null;
    }

    private Index getIndex() {
        return field.getAnnotation(Index.class);
    }

    public boolean isNumber() {
        if (Number.class.isAssignableFrom(field.getType())) {
            return true;
        }
        String name = field.getType().getName();
        return name.equals("int") || name.equals("long") || name.equals("double");
    }

    public boolean isInt() {
        return Integer.class.isAssignableFrom(field.getType()) || field.getType().getName().equals("int");
    }

    public boolean isLong() {
        return Long.class.isAssignableFrom(field.getType()) || field.getType().getName().equals("long");
    }

    public boolean isDate() {
        return Date.class.isAssignableFrom(field.getType());
    }

    private boolean isString() {
        return String.class.isAssignableFrom(field.getType());
    }

}
