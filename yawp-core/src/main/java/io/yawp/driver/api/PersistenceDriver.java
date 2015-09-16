package io.yawp.driver.api;

import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;

public interface PersistenceDriver {

	public void save(Object object);

	public <T> FutureObject<T> saveAsync(Object object);

	public void destroy(IdRef<?> id);

}
