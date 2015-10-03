package io.yawp.driver.postgresql.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionPool {

	private static final String JDBC_YAWP_TEST = "jdbc/yawp_test";

	public static Connection connection() {
		return connection(true);
	}

	public static Connection connection(boolean autoCommit) {
		try {
			Context ctx = (Context) new InitialContext().lookup("java:comp/env");
			DataSource ds = (DataSource) ctx.lookup(JDBC_YAWP_TEST);
			Connection connection = ds.getConnection();
			connection.setAutoCommit(autoCommit);
			return connection;
		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void rollbackAndClose(Connection connection) {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(connection);
		}
	}

	public static void commitAndClose(Connection connection) {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(connection);
		}
	}

}
