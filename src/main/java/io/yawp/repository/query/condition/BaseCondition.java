package io.yawp.repository.query.condition;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.Collection;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;

public abstract class BaseCondition {

	protected static final Class<?>[] VALID_ID_CLASSES = new Class<?>[] { IdRef.class, Long.class, String.class, Key.class };

	public abstract Filter getPredicate(Class<?> clazz) throws FalsePredicateException;

	public boolean evaluate(Object object) {
		return true;
	}

	public abstract BaseCondition not();

	public abstract void normalizeIdRefs(Class<?> clazz, Repository r);

	public abstract Class<?> getIdTypeFor(Class<?> clazz);

	public boolean isByIdFor(Class<?> clazz) {
		return this.getIdTypeFor(clazz) != null;
	}

	public BaseCondition and(BaseCondition c) {
		return Condition.and(this, c);
	}

	public BaseCondition or(BaseCondition c) {
		return Condition.or(this, c);
	}

	protected static void assertList(Object object) {
		getContentType(object);
	}

	protected static Class<?> getContentType(Object object) {
		Class<?> clazz = object.getClass();
		if (clazz.isArray()) {
			return clazz.getComponentType();
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			Collection<?> c = (Collection<?>) object;
			if (c.isEmpty()) {
				return null;
			}
			return c.iterator().next().getClass();
		}
		throw new RuntimeException("Unsupported 'in' type: must be a primtive array or a Collection<?>. Found " + clazz.getSimpleName());
	}

	protected static boolean isValidIdClass(Object value, boolean list) {
		Class<?> actualClazz;
		if (list) {
			actualClazz = getContentType(value);
			if (actualClazz == null) {
				return true;
			}
		} else {
			actualClazz = value.getClass();
		}

		for (Class<?> validClass : VALID_ID_CLASSES) {
			if (validClass.isAssignableFrom(actualClazz)) {
				return true;
			}
		}
		return false;
	}

}
