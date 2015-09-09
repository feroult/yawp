package io.yawp.repository.driver.api;

import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.FutureObject;
import io.yawp.repository.Repository;

public interface RepositoryDriver {

	void init(Repository r);

	public void save(ObjectHolder objectH);

	public <T> FutureObject<T> saveAsync(ObjectHolder objectH, boolean enableHooks);

}
