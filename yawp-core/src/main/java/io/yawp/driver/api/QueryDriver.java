package io.yawp.driver.api;

import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

public interface QueryDriver {

    <T> List<T> objects(QueryBuilder<?> builder);

    <T> List<IdRef<T>> ids(QueryBuilder<?> builder);

    <T> T fetch(IdRef<T> id);

    <T> FutureObject<T> fetchAsync(IdRef<T> id);
}
