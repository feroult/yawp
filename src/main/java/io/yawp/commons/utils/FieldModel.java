package io.yawp.commons.utils;

import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;

import java.lang.reflect.Field;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class FieldModel {

	private Field field;

	public FieldModel(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public boolean isControl() {
		return Key.class.equals(field.getType()) || field.isAnnotationPresent(Id.class);
	}

	private boolean isSaveAsJson() {
		return field.getAnnotation(Json.class) != null;
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

}
