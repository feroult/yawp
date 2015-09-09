package io.yawp.repository.driver.api;

import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;

public interface RepositoryDriver {

	void init(Repository r);

	public PersistenceDriver persistence();

	public QueryDriver query(QueryBuilder<?> builder);

}
