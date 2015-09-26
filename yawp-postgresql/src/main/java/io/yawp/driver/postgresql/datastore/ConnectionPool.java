package io.yawp.driver.postgresql.datastore;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionPool {

	private static final String JDBC_YAWP_TEST = "jdbc/yawp_test";

	public synchronized static Connection connection() {
		try {
			Context ctx = (Context) new InitialContext().lookup("java:comp/env");
			DataSource ds = (DataSource) ctx.lookup(JDBC_YAWP_TEST);
			Connection connection = ds.getConnection();
			return connection;
		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized static void close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
