package io.yawp.repository.query.condition;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;

public class ConditionReference {

	private static final String PARENT_REF_KEYWORK = "parent";

	private String[] split;

	private int current;

	private Object object;

	private Class<?> clazz;

	private String refString;

	public ConditionReference(String refString, Class<?> clazz, Object object) {
		this.refString = refString;
		this.clazz = clazz;
		this.object = object;

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

	public Object getValue() throws AncestorConditionForChildException {
		verifyAncestorConditionForChild();

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

	private void verifyAncestorConditionForChild() throws AncestorConditionForChildException {
		if (clazz != null && hasStartedWithAncestorObject() && isAncestorChild()) {
			throw new AncestorConditionForChildException();
		}
	}

	private Object advanceAncestorSequenceIfNecessary() {
		if (hasStartedWithAncestorObject()) {
			advanceToTheRightAncestor();
		}

		if (!isParentRef()) {
			return object;
		}

		IdRef<?> parentId = EntityUtils.getParentId(object);
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
			ancestorClazz = EntityUtils.getAncestorClazz(i, clazz);
			if (ancestorClazz == null) {
				throw new RuntimeException("Invalid condition ref " + refString + " for object class: " + object.getClass().getName());
			}
			nextRef();
		}
	}

	private boolean hasStartedWithAncestorObject() {
		return !object.getClass().equals(clazz);
	}

	private boolean isAncestorChild() {
		int ancestorRefNumber = -1;
		for (int i = 0; i < split.length - 1; i++) {
			if (!split[i].equals(PARENT_REF_KEYWORK)) {
				break;
			}
			ancestorRefNumber++;
		}

		int ancestorNumber = EntityUtils.getAncestorNumber(clazz, object.getClass());
		return ancestorNumber > ancestorRefNumber;
	}

}
