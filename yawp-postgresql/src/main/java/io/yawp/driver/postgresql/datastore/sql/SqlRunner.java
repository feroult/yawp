package io.yawp.driver.postgresql.datastore.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SqlRunner {

	@SuppressWarnings("serial")
	private class NotImplementedException extends RuntimeException {

	}

	protected String sql;

	private Connection connection;

	public SqlRunner(Connection connection, String sql) {
		this.connection = connection;
		this.sql = sql;
	}

	protected SqlRunner(Connection connection) {
		this.connection = connection;
	}

	protected void prepare(PreparedStatement ps) throws SQLException {

	}

	protected Object collect(ResultSet rs) throws SQLException {
		throw new NotImplementedException();
	}

	protected Object collectSingle(ResultSet rs) throws SQLException {
		return null;
	}

	protected void setSql(String sql) {
		this.sql = sql;
	}

	@SuppressWarnings("unchecked")
	public <T> T executeQuery() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(sql);
			prepare(ps);

			rs = ps.executeQuery();

			try {

				return (T) collect(rs);

			} catch (NotImplementedException e) {
				if (!rs.next()) {
					return null;
				}
				return (T) collectSingle(rs);
			}

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

	public void execute() {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(sql);
			prepare(ps);

			ps.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
