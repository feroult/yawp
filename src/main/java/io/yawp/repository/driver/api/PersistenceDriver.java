package io.yawp.repository.driver.api;

import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.FutureObject;

public interface PersistenceDriver {

	public void save(ObjectHolder objectH);

	public <T> FutureObject<T> saveAsync(ObjectHolder objectH, boolean enableHooks);

}
