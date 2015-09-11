package io.yawp.driver.api;

import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;

public interface PersistenceDriver {

	public void save(ObjectHolder objectH);

	public <T> FutureObject<T> saveAsync(ObjectHolder objectH, boolean enableHooks);

	public void destroy(IdRef<?> id);

}
