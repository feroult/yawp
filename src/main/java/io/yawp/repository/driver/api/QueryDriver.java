package io.yawp.repository.driver.api;

import io.yawp.repository.query.condition.FalsePredicateException;

import java.util.List;

public interface QueryDriver {

	public <T> List<T> execute() throws FalsePredicateException;
}
