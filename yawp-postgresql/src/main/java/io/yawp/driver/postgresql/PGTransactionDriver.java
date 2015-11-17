package io.yawp.driver.postgresql;

import io.yawp.driver.api.TransactionDriver;
import io.yawp.driver.postgresql.sql.ConnectionManager;

public class PGTransactionDriver implements TransactionDriver {

    private ConnectionManager connectionManager;

    public PGTransactionDriver(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public TransactionDriver begin() {
        connectionManager.beginTransaction();
        return this;
    }

    @Override
    public TransactionDriver beginX() {
        return begin();
    }

    @Override
    public void rollback() {
        connectionManager.rollback();
    }

    @Override
    public void commit() {
        connectionManager.commit();
    }

}
