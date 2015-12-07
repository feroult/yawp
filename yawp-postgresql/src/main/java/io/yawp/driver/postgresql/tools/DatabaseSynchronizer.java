package io.yawp.driver.postgresql.tools;

import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.ObjectModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseSynchronizer {

    private static final String SQL_CATALOG_SELECT = "SELECT c.* FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace";

    private static final String SQL_CATALOG_TABLES = "WHERE c.relkind = 'r' AND n.nspname = ANY (CURRENT_SCHEMAS(false))";

    private static final String SQL_CREATE_TABLE = "create table %s (id bigserial primary key, key jsonb, properties jsonb)";

    private ConnectionManager connectionManager = new ConnectionManager();

    public void sync(Set<Class<?>> endpointClazzes) {
        List<String> existingTables = getExistingTables();

        for (Class<?> endpointClazz : endpointClazzes) {
            sync(existingTables, endpointClazz);
        }
    }

    protected List<String> getExistingTables() {
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

        return connectionManager.executeQuery(runner);
    }

    private void sync(List<String> existingTables, Class<?> endpointClazz) {
        ObjectModel model = new ObjectModel(endpointClazz);

        if (existingTables.contains(model.getKind())) {
            return;
        }

        createTable(model.getKind());
    }

    private void createTable(String kind) {
        connectionManager.execute(String.format(SQL_CREATE_TABLE, kind));
    }

    public void recreate(String schema) {
        connectionManager.execute(String.format("drop schema %s cascade; create schema %s;", schema, schema));
    }

    public void truncateAll() {
        List<String> existingTables = getExistingTables();
        for (String table : existingTables) {
            truncate(table);
        }
    }

    private void truncate(String table) {
        connectionManager.execute(String.format("truncate table %s cascade", table));
    }

}
