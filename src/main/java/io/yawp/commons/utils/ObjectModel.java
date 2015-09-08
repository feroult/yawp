package io.yawp.commons.utils;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

import java.lang.reflect.Field;

public class ObjectModel {

	private Class<?> clazz;

	public ObjectModel(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Field getIdField() {
		return ReflectionUtils.getFieldWithAnnotation(clazz, Id.class);
	}

	public Field getParentField() {
		return ReflectionUtils.getFieldWithAnnotation(clazz, ParentId.class);
	}

	public boolean isIdRef(Field field) {
		return IdRef.class.isAssignableFrom(field.getType());
	}

	public Class<?> getParentClazz() {
		Field field = getParentField();
		if (field == null) {
			return null;
		}
		return (Class<?>) ReflectionUtils.getGenericParameter(clazz);
	}

}
