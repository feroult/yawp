package io.yawp.driver.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.ConnectionPoolDataSource;

public class Datastore {

	private Connection connection() {
		try {
			Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
			ConnectionPoolDataSource ds = (ConnectionPoolDataSource) envCtx.lookup("jdbc/yawp_test");
			return ds.getPooledConnection().getConnection();
		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(Object key) {
		// TODO Auto-generated method stub

	}

	public Key put(Entity entity) {

		Connection connection = connection();

		try {
			PreparedStatement prepareStatement = connection.prepareStatement("select * from xpto");
			prepareStatement.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		// TODO Auto-generated method stub
		return null;
	}

	public Entity get(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

}
