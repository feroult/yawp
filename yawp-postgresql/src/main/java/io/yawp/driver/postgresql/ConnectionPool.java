package io.yawp.driver.postgresql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.ConnectionPoolDataSource;

public class ConnectionPool {

	private static final String JDBC_YAWP_TEST = "jdbc/yawp_test";

	public static Connection connection() {
		try {
			Context ctx = (Context) new InitialContext().lookup("java:comp/env");
			ConnectionPoolDataSource ds = (ConnectionPoolDataSource) ctx.lookup(JDBC_YAWP_TEST);
			return ds.getPooledConnection().getConnection();
		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
