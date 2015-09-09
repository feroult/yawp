package io.yawp.repository.driver.api;

import io.yawp.repository.query.DatastoreQuery;

import java.util.List;

public interface QueryDriver {

	public <T> List<T> execute(DatastoreQuery<T> builder);
}
