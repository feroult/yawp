package io.yawp.driver.postgresql.datastore.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SqlRunner {

	private String sql;

	private Connection connection;

	public SqlRunner(Connection connection, String sql) {
		this.connection = connection;
		this.sql = sql;
	}

	protected void prepare(PreparedStatement ps) {

	}

	protected void collect(ResultSet rs) throws SQLException {

	}

	public void run() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(sql);
			prepare(ps);

			rs = ps.executeQuery();
			collect(rs);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
