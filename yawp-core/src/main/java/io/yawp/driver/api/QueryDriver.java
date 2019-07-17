package io.yawp.driver.api;

import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.Map;

public interface QueryDriver {

	<T> List<T> objects(QueryBuilder<?> builder);

	<T> List<IdRef<T>> ids(QueryBuilder<?> builder);

	<T> T fetch(IdRef<T> id);

	<T> Map<IdRef<T>, T> fetchAll(List<IdRef<T>> ids);

	<T> FutureObject<T> fetchAsync(IdRef<T> id);
}
