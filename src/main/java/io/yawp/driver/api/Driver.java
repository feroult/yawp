package io.yawp.driver.api;

import io.yawp.repository.Repository;

public interface Driver {

	void init(Repository r);

	public PersistenceDriver persistence();

	public QueryDriver query();

	public NamespaceDriver namespace();

	public TransactionDriver transaction();

	public TestHelperDriver tests();

	public EnvironmentDriver environment();

	public HelpersDriver helpers();

}
