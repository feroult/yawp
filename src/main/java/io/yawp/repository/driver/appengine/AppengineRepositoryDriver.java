package io.yawp.repository.driver.appengine;

import io.yawp.repository.Repository;
import io.yawp.repository.driver.api.PersistenceDriver;
import io.yawp.repository.driver.api.QueryDriver;
import io.yawp.repository.driver.api.RepositoryDriver;
import io.yawp.repository.query.QueryBuilder;

public class AppengineRepositoryDriver implements RepositoryDriver {

	private Repository r;

	@Override
	public void init(Repository r) {
		this.r = r;
	}

	@Override
	public PersistenceDriver persistence() {
		return new AppenginePersistenceDriver(r);
	}

	@Override
	public QueryDriver query(QueryBuilder<?> builder) {
		return new AppengineQueryDriver(builder);
	}

}
