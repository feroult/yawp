package io.yawp.commons.utils;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;

import java.lang.reflect.Field;
import java.util.List;

import com.google.appengine.api.datastore.Key;

class FieldModel {

	private Field field;

	FieldModel(Field field) {
		this.field = field;
		field.setAccessible(true);
	}

	Field getField() {
		return field;
	}

	String getName() {
		return field.getName();
	}

	public Object getValue(Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isControl() {
		return Key.class.equals(field.getType()) || field.isAnnotationPresent(Id.class);
	}

	private boolean isList() {
		return List.class.isAssignableFrom(field.getType());
	}

	public boolean isSaveAsList() {
		return isList() && !isSaveAsJson();
	}

	public boolean hasIndex() {
		return field.getAnnotation(Index.class) != null;
	}

	public boolean isString(Field field) {
		return String.class.isAssignableFrom(field.getType());
	}

	public boolean isIndexNormalizable() {
		if (!hasIndex()) {
			throw new RuntimeException("You must add @Index annotation the the field '" + field.getName()
					+ "' if you want to use it as a index in where statements.");
		}
		return getIndex().normalize() && isString(field);
	}

	public boolean isEnum(Object value) {
		return value != null && value.getClass().isEnum();
	}

	public boolean isEnum() {
		return field.getType().isEnum();
	}

	public boolean isIdRef() {
		return IdRef.class.isAssignableFrom(field.getType());
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

}
