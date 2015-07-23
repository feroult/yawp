package io.yawp.repository.query.condition;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;

public abstract class BaseCondition {

	protected static final Class<?>[] VALID_ID_CLASSES = new Class<?>[] { IdRef.class, Long.class, String.class, Key.class };

	public abstract void init(Repository r, Class<?> clazz);

	public abstract Filter getPredicate() throws FalsePredicateException;

	public abstract boolean evaluate(Object object);

	public abstract BaseCondition not();

	public BaseCondition and(BaseCondition c) {
		return Condition.and(this, c);
	}

	public BaseCondition or(BaseCondition c) {
		return Condition.or(this, c);
	}
}
