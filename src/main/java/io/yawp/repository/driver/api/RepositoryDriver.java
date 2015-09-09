package io.yawp.repository.driver.api;

import io.yawp.repository.Repository;

public interface RepositoryDriver {

	void init(Repository r);

	public PersistenceDriver persistence();

}
