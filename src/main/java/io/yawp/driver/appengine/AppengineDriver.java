package io.yawp.driver.appengine;

import io.yawp.driver.api.Driver;
import io.yawp.driver.api.EnvironmentDriver;
import io.yawp.driver.api.NamespaceDriver;
import io.yawp.driver.api.PersistenceDriver;
import io.yawp.driver.api.QueryDriver;
import io.yawp.driver.api.TestHelperDriver;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.Repository;

public class AppengineDriver implements Driver {

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
	public QueryDriver query() {
		return new AppengineQueryDriver(r);
	}

	@Override
	public NamespaceDriver namespace() {
		return new AppengineNamespaceDriver();
	}

	@Override
	public TransactionDriver transaction() {
		return new AppengineTransationDriver();
	}

	@Override
	public TestHelperDriver tests() {
		return new AppengineTestHelperDriver();
	}

	@Override
	public EnvironmentDriver environment() {
		return new AppengineEnvironmentDriver();
	}
}
