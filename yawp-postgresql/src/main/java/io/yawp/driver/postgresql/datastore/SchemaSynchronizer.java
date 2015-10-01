package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.ObjectModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchemaSynchronizer {

	private static final String SQL_CATALOG_SELECT = "SELECT c.* FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace";

	private static final String SQL_CATALOG_TABLES = "WHERE c.relkind = 'r' AND n.nspname = ANY (CURRENT_SCHEMAS(false))";

	private static final String SQL_CREATE_TABLE = "create table %s (id bigserial primary key, key jsonb, properties jsonb)";

	public static void sync(Set<Class<?>> endpointClazzes) {
		List<String> existingTables = getExistingTables();

		for (Class<?> endpointClazz : endpointClazzes) {
			sync(existingTables, endpointClazz);
		}
	}

	protected static List<String> getExistingTables() {
		String sql = String.format("%s %s", SQL_CATALOG_SELECT, SQL_CATALOG_TABLES);

		SqlRunner runner = new SqlRunner(sql) {
			@Override
			public List<String> collect(ResultSet rs) throws SQLException {
				List<String> tables = new ArrayList<String>();

				while (rs.next()) {
					tables.add(rs.getString("relname"));
				}

				return tables;
			}
		};

		return runner.executeQuery();
	}

	private static void sync(List<String> existingTables, Class<?> endpointClazz) {
		ObjectModel model = new ObjectModel(endpointClazz);

		if (existingTables.contains(model.getKind())) {
			return;
		}

		createTable(model.getKind());
	}

	private static void createTable(String kind) {
		new SqlRunner(String.format(SQL_CREATE_TABLE, kind)).execute();
	}

	public static void recreate(String schema) {
		new SqlRunner(String.format("drop schema %s cascade; create schema %s;", schema)).execute();
	}

	public static void truncateAll() {
		List<String> existingTables = getExistingTables();
		for (String table : existingTables) {
			truncate(table);
		}
	}

	private static void truncate(String table) {
		new SqlRunner(String.format("truncate table %s cascade", table)).execute();
	}

}
