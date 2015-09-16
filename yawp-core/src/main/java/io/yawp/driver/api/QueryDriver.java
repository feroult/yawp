package io.yawp.driver.api;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

public interface QueryDriver {

	public <T> List<T> objects(QueryBuilder<?> builder);

	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder);

	public <T> T fetch(IdRef<T> id);
}
