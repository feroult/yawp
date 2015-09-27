package io.yawp.driver.postgresql.connection;

import java.sql.Connection;

public class ConnectionManager {

	private Connection connection;

	private Connection connection() {
		if (connection != null) {
			return connection;
		}
		connection = ConnectionPool.connection();
		return connection;
	}

	public void dispose() {
		if (connection == null) {
			return;
		}
		ConnectionPool.close(connection);
	}

	public <T> T executeQuery(SqlRunner runner) {
		return runner.executeQuery(connection());
	}

	public void execute(SqlRunner runner) {
		runner.execute(connection());
	}

}
