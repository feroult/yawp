package io.yawp.driver.postgresql;

import io.yawp.driver.api.*;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.repository.Repository;

public class PGDriver implements Driver {

    private Repository r;

    private ConnectionManager connectionManager;

    @Override
    public void init(Repository r) {
        this.r = r;
        this.connectionManager = new ConnectionManager();
    }

    @Override
    public PersistenceDriver persistence() {
        return new PGPersistenceDriver(r, connectionManager);
    }

    @Override
    public QueryDriver query() {
        return new PGQueryDriver(r, connectionManager);
    }

    @Override
    public NamespaceDriver namespace() {
        return new PGNamespaceDriver();
    }

    @Override
    public TransactionDriver transaction() {
        return new PGTransactionDriver(connectionManager);
    }

    @Override
    public EnvironmentDriver environment() {
        return new PGEnvironmentDriver();
    }

    @Override
    public HelpersDriver helpers() {
        return new PGHelpersDriver();
    }

    @Override
    public PipesDriver pipes() {
        return null;
    }

}
