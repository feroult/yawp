package io.yawp.repository.driver.api;

import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.Repository;

public interface RepositoryDriver {

	void init(Repository r);

	public void save(ObjectHolder objectH);

}
