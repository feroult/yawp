package io.yawp.driver.postgresql.datastore.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SqlRunner {

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

	protected <T> T collect(ResultSet rs) throws SQLException {
		return null;
	}

	protected Object collectScalar(ResultSet rs) throws SQLException {
		return null;
	}

	protected void setSql(String sql) {
		this.sql = sql;
	}

	public <T> T executeQuery() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(sql);
			prepare(ps);

			rs = ps.executeQuery();
			return collect(rs);

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

	public boolean getBoolean() {
		return (Boolean) getScalar();
	}

	private Object getScalar() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(sql);
			prepare(ps);

			rs = ps.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return collectScalar(rs);

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
