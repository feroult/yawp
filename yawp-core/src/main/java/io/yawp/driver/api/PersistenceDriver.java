package io.yawp.driver.api;

import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;

public interface PersistenceDriver {

    void save(Object object);

    <T> FutureObject<T> saveAsync(Object object);

    void destroy(IdRef<?> id);

}
