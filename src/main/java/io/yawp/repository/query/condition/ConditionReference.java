package io.yawp.repository.query.condition;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;

public class ConditionReference {

	private static final String PARENT_REF_KEYWORK = "parent";

	private String[] split;

	private int current;

	private Object object;

	public ConditionReference(String refString, Object object) {
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

	public Object getValue() {
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

	private Object advanceAncestorSequenceIfNecessary() {
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

}
