package io.yawp.driver.postgresql.sql;

import java.sql.Connection;

public class ConnectionManager {

	private Connection connection;

	private Connection getConnection() {
		if (isTransactionInProgress()) {
			return connection;
		}
		return ConnectionPool.connection();
	}

	private void returnToPool(Connection connection) {
		ConnectionPool.close(connection);
	}

	private boolean isTransactionInProgress() {
		return this.connection != null;
	}

	public <T> T executeQuery(SqlRunner runner) {
		Connection connection = getConnection();
		try {
			return runner.executeQuery(connection);
		} finally {
			if (!isTransactionInProgress()) {
				returnToPool(connection);
			}
		}
	}

	public void execute(SqlRunner runner) {
		Connection connection = getConnection();
		try {
			runner.execute(connection);
		} finally {
			if (!isTransactionInProgress()) {
				returnToPool(connection);
			}
		}
	}

	public synchronized void beginTransaction() {
		if (isTransactionInProgress()) {
			throw new RuntimeException("Another transaction already in progress");
		}

		this.connection = ConnectionPool.connection(false);
	}

	public synchronized void rollback() {
		if (!isTransactionInProgress()) {
			throw new RuntimeException("No transaction already in progress");
		}
		ConnectionPool.rollbackAndClose(connection);
		this.connection = null;
	}

	public synchronized void commit() {
		if (!isTransactionInProgress()) {
			throw new RuntimeException("No transaction already in progress");
		}
		ConnectionPool.commitAndClose(connection);
		this.connection = null;
	}

}
