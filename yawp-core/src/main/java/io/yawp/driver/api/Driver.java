package io.yawp.driver.api;

import io.yawp.repository.Repository;

public interface Driver {

	public void init(Repository r);

	public PersistenceDriver persistence();

	public QueryDriver query();

	public NamespaceDriver namespace();

	public TransactionDriver transaction();

	public EnvironmentDriver environment();

	public HelpersDriver helpers();

}
