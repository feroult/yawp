package io.yawp.driver.mock;

import io.yawp.driver.api.Driver;
import io.yawp.driver.api.EnvironmentDriver;
import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.api.NamespaceDriver;
import io.yawp.driver.api.PersistenceDriver;
import io.yawp.driver.api.QueryDriver;
import io.yawp.driver.api.TestHelperDriver;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.Repository;

public class MockDriver implements Driver {

	@Override
	public void init(Repository r) {
		// TODO Auto-generated method stub
		System.out.println("mock!");
	}

	@Override
	public PersistenceDriver persistence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryDriver query() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamespaceDriver namespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionDriver transaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestHelperDriver tests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnvironmentDriver environment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HelpersDriver helpers() {
		// TODO Auto-generated method stub
		return null;
	}

}
