package io.yawp.driver.mock;

import io.yawp.driver.api.*;
import io.yawp.repository.Repository;

public class MockDriver implements Driver {

    private Repository r;

    @Override
    public void init(Repository r) {
        this.r = r;
    }

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public PersistenceDriver persistence() {
        return new MockPersistenceDriver(r);
    }

    @Override
    public QueryDriver query() {
        return new MockQueryDriver();
    }

    @Override
    public NamespaceDriver namespace() {
        return new MockNamespaceDriver();
    }

    @Override
    public TransactionDriver transaction() {
        return new MockTransactionDriver();
    }

    @Override
    public EnvironmentDriver environment() {
        return new MockEnvironmentDriver();
    }

    @Override
    public HelpersDriver helpers() {
        return new MockHelpersDriver();
    }

    @Override
    public PipesDriver pipes() {
        return new MockPipesDriver(r);
    }

}
