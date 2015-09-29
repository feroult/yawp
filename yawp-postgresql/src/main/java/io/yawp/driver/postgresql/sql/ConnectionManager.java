package io.yawp.driver.postgresql.sql;

import java.sql.Connection;

public class ConnectionManager {

	private Connection getConnection() {
		if (isTransactionInProgress()) {
			return null;
		}
		return ConnectionPool.connection();
	}

	private void returnToPool(Connection connection) {
		ConnectionPool.close(connection);
	}

	private boolean isTransactionInProgress() {
		// TODO
		return false;
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

}
