package io.yawp.repository.driver.api;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.condition.FalsePredicateException;

import java.util.List;

public interface QueryDriver {

	// TODO: driver - remove this exception?
	public <T> List<T> objects(QueryBuilder<?> builder) throws FalsePredicateException;

	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) throws FalsePredicateException;

	public <T> T fetch(IdRef<T> id);
}
