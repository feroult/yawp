package io.yawp.repository.query.condition;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;

public abstract class BaseCondition {

	protected static final Class<?>[] VALID_ID_CLASSES = new Class<?>[] { IdRef.class, Long.class, String.class, Key.class };

	public abstract void init(Repository r, Class<?> clazz);

	public abstract boolean hasPreFilter();

	public abstract boolean hasPostFilter();

	public abstract Filter createPreFilter() throws FalsePredicateException;

	public abstract boolean evaluate(Object object);

	public abstract BaseCondition not();

	public BaseCondition and(BaseCondition c) {
		return Condition.and(this, c);
	}

	public BaseCondition or(BaseCondition c) {
		return Condition.or(this, c);
	}

	public <T> List<T> applyPostFilter(List<T> objects) {
		List<T> result = new ArrayList<T>();

		for (T object : objects) {
			if (!evaluate(object)) {
				continue;
			}
			result.add(object);
		}

		return result;
	}

}
