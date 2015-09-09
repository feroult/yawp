package io.yawp.repository.query.condition;

import io.yawp.commons.utils.ObjectHolder;
import io.yawp.commons.utils.ObjectModel;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;

public class ConditionReference {

	private static final String PARENT_REF_KEYWORK = "parent";

	private String[] split;

	private int current;

	private Object object;

	private ObjectHolder objectH;

	private Class<?> clazz;

	private ObjectModel model;

	private String refString;

	public ConditionReference(String refString, Class<?> clazz, Object object) {
		this.refString = refString;
		this.clazz = clazz;
		this.model = new ObjectModel(clazz);
		this.object = object;
		this.objectH = new ObjectHolder(object);

		this.split = refString.split("->");
		this.current = 0;
	}

	private boolean hasMoreRefs() {
		return current < split.length - 1;
	}

	private String nextRef() {
		return split[current++];
	}

	private boolean isParentRef() {
		return split[current].equalsIgnoreCase(PARENT_REF_KEYWORK);
	}

	private String fieldName() {
		return split[split.length - 1];
	}

	public Object getValue() throws ConditionForChildException {
		verifyIfConditionIsForChild();

		Object currentObject = advanceAncestorSequenceIfNecessary();

		while (hasMoreRefs()) {
			IdRef<?> objectId = (IdRef<?>) ReflectionUtils.getFieldValue(currentObject, nextRef());

			if (objectId == null) {
				return null;
			}

			currentObject = objectId.fetch();
		}

		return ReflectionUtils.getFieldValue(currentObject, fieldName());
	}

	private void verifyIfConditionIsForChild() throws ConditionForChildException {
		if (clazz != null && isObjectAcenstor() && isReferenceForChild()) {
			throw new ConditionForChildException();
		}
	}

	private Object advanceAncestorSequenceIfNecessary() {
		if (isObjectAcenstor()) {
			advanceToTheRightAncestor();
		}

		if (!isParentRef()) {
			return object;
		}

		IdRef<?> parentId = objectH.getParentId();
		nextRef();

		while (isParentRef()) {
			parentId = parentId.getParentId();
			nextRef();
		}

		return parentId.fetch();
	}

	private void advanceToTheRightAncestor() {
		if (clazz == null || object.getClass().equals(clazz)) {
			return;
		}

		Class<?> ancestorClazz = clazz;

		for (int i = 0; !object.getClass().equals(ancestorClazz); i++) {
			ancestorClazz = model.getAncestorClazz(i);
			if (ancestorClazz == null) {
				throw new RuntimeException("Invalid condition ref " + refString + " for object class: " + object.getClass().getName());
			}
			nextRef();
		}
	}

	private boolean isObjectAcenstor() {
		return !object.getClass().equals(clazz);
	}

	private boolean isReferenceForChild() {
		int ancestorRefNumber = -1;
		for (int i = 0; i < split.length - 1; i++) {
			if (!split[i].equals(PARENT_REF_KEYWORK)) {
				break;
			}
			ancestorRefNumber++;
		}

		return model.getAncestorNumber(object.getClass()) > ancestorRefNumber;
	}

}
