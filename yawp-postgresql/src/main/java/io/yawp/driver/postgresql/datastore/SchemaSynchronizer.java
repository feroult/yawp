package io.yawp.driver.postgresql.datastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchemaSynchronizer {

	private static final String SQL_CATALOG_SELECT = "SELECT c.* FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace";
	private static final String SQL_CATALOG_TABLES = "WHERE c.relkind = 'r' AND n.nspname = ANY (CURRENT_SCHEMAS(false))";

	public static void sync(Set<Class<?>> endpointClazzes) {
		Connection connection = ConnectionPool.connection();

		try {
			List<String> existingTables = getExistingTables(connection);

			for (Class<?> endpointClazz : endpointClazzes) {
				sync(connection, endpointClazz);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionPool.close(connection);
		}
	}

	private static List<String> getExistingTables(Connection connection) throws SQLException {
		List<String> tables = new ArrayList<String>();

		String sql = String.format("%s %s", SQL_CATALOG_SELECT, SQL_CATALOG_TABLES);

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				tables.add(rs.getString("relname"));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}

		return tables;
	}

	private static void sync(Connection connection, Class<?> endpointClazz) {

	}

}
