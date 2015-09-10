package io.yawp.repository;

import java.lang.reflect.Field;

// TODO: create the object holders/model at the right flow stage
public class ObjectHolder {

	private ObjectModel model;

	private Object object;

	public ObjectHolder(Object object) {
		this.object = object;
		this.model = new ObjectModel(object.getClass());
	}

	public ObjectModel getModel() {
		return model;
	}

	public Object getObject() {
		return object;
	}

	public void setId(IdRef<?> id) {
		Field idField = model.getIdField();
		try {
			idField.set(object, id);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public IdRef<?> getId() {
		Field field = model.getIdField();

		if (!model.isIdRef(field)) {
			throw new RuntimeException("@Id must be " + IdRef.class.getSimpleName());
		}

		try {

			return (IdRef<?>) field.get(object);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void setParentId(IdRef<?> parentId) {
		Field parentIdField = model.getParentField();
		if (parentIdField == null) {
			if (parentId != null) {
				throw new RuntimeException("Trying to set parentId " + parentId + " to class " + object.getClass().getSimpleName()
						+ ", but it doesn't seem to have a @Parent field.");
			}
			return;
		}

		try {
			parentIdField.set(object, parentId);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public IdRef<?> getParentId() {
		Field parentField = model.getParentField();
		if (parentField == null) {
			return null;
		}

		try {

			return (IdRef<?>) parentField.get(object);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
