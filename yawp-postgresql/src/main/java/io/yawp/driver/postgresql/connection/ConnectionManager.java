package io.yawp.driver.postgresql.connection;

import java.sql.Connection;

public class ConnectionManager {

	private Connection connection() {
		if (isTransactionInProgress()) {
			return null;
		}
		return ConnectionPool.connection();
	}

	private boolean isTransactionInProgress() {
		// TODO
		return false;
	}

	@Deprecated
	public void dispose() {
	}

	public <T> T executeQuery(SqlRunner runner) {
		Connection connection = connection();
		try {
			return runner.executeQuery(connection);
		} finally {
			if (!isTransactionInProgress()) {
				ConnectionPool.close(connection);
			}
		}
	}

	public void execute(SqlRunner runner) {
		Connection connection = connection();
		try {
			runner.execute(connection);
		} finally {
			if (!isTransactionInProgress()) {
				ConnectionPool.close(connection);
			}
		}
	}

}
