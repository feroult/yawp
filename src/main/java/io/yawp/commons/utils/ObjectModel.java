package io.yawp.commons.utils;

import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ObjectModel {

	private Class<?> clazz;

	public ObjectModel(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getKind() {
		return KindResolver.getKindFromClass(clazz);
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
		Field parentField = getParentField();
		if (parentField == null) {
			return null;
		}
		return (Class<?>) ReflectionUtils.getGenericParameter(parentField);
	}

	public Class<?> getAncestorClazz(int ancestor) {
		Class<?> parentClazz = clazz;
		for (int i = 0; i <= ancestor; i++) {
			ObjectModel model = new ObjectModel(parentClazz);
			parentClazz = model.getParentClazz();
		}
		return parentClazz;
	}

	public int getAncestorNumber(Class<?> ancestorClazz) {
		if (clazz.equals(ancestorClazz)) {
			return -1;
		}

		Class<?> parentClazz = getParentClazz();
		int ancestorNumber = 0;

		while (parentClazz != null && !parentClazz.equals(ancestorClazz)) {
			ObjectModel model = new ObjectModel(parentClazz);
			parentClazz = model.getParentClazz();
			ancestorNumber++;
		}

		if (parentClazz == null) {
			throw new RuntimeException("Invalid ancestor " + ancestorClazz.getName() + " for class " + clazz.getName());
		}

		return ancestorNumber;
	}

	public List<FieldModel> getFieldModels() {
		List<Field> fields = ReflectionUtils.getFieldsRecursively(clazz);

		List<FieldModel> fieldModels = new ArrayList<FieldModel>();

		for (Field field : fields) {
			fieldModels.add(new FieldModel(field));
		}

		return fieldModels;
	}

	public FieldModel getFieldModel(String fieldName) {
		return new FieldModel(ReflectionUtils.getFieldRecursively(clazz, fieldName));
	}
}
