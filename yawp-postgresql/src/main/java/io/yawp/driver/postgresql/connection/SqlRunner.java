package io.yawp.driver.postgresql.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgresql.util.PGobject;

public class SqlRunner {

	@SuppressWarnings("serial")
	private class NotImplementedException extends RuntimeException {

	}

	protected String sql;

	protected Map<String, PlaceHolder> placeHolders = new HashMap<String, PlaceHolder>();

	public SqlRunner(String sql) {
		this.sql = sql;
		init();
	}

	protected void init() {
		bind();
		parsePlaceHoldersIndexes();
		removePlaceHolders();
	}

	protected void bind() {
	}

	protected void prepare(PreparedStatement ps) throws SQLException {
	}

	protected Object collect(ResultSet rs) throws SQLException {
		throw new NotImplementedException();
	}

	protected Object collectSingle(ResultSet rs) throws SQLException {
		return null;
	}

	protected void bind(String placeHolderKey, PlaceHolder placeHolderObject) {
		placeHolders.put(":" + placeHolderKey, placeHolderObject);
	}

	private void prepareInternal(PreparedStatement ps) throws SQLException {
		prepare(ps);
		prepareBinded(ps);
	}

	private void prepareBinded(PreparedStatement ps) throws SQLException {
		for (PlaceHolder placeHolder : placeHolders.values()) {
			placeHolder.setValue(ps);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T executeQuery(Connection connection) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(sql);
			prepareInternal(ps);

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

	public void execute(Connection connection) {
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(sql);
			prepareInternal(ps);

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

	private void parsePlaceHoldersIndexes() {
		Pattern pattern = Pattern.compile("\\:\\w+");
		Matcher matcher = pattern.matcher(sql);

		int index = 1;

		while (matcher.find()) {
			String key = matcher.group();
			PlaceHolder placeHolder = placeHolders.get(key);
			placeHolder.addIndex(index++);
		}

		return;
	}

	private void removePlaceHolders() {
		for (String placeHolderKey : placeHolders.keySet()) {
			sql = sql.replaceAll(placeHolderKey, "?");
		}
	}

	protected PGobject createJsonObject(String json) {
		try {
			PGobject jsonObject = new PGobject();
			jsonObject.setType("jsonb");
			jsonObject.setValue(json);
			return jsonObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
