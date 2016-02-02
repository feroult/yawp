package io.yawp.driver.postgresql.tools;

import io.yawp.driver.postgresql.configuration.DataSourceInfo;
import io.yawp.driver.postgresql.configuration.JettyConfiguration;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.models.ObjectModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseSynchronizer {

    private static final String SQL_DATABASE_EXISTS = "SELECT 1 AS result FROM pg_database WHERE datname = :database";

    private static final String SQL_DATABASE_KILL_ACTIVITY = "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE datname = :database AND state='idle'";

    private static final String SQL_DATABASE_DROP = "DROP DATABASE %s;";

    private static final String SQL_DATABASE_CREATE = "CREATE DATABASE %s;";

    private static final String SQL_CATALOG_SELECT = "SELECT c.* FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace";

    private static final String SQL_CATALOG_TABLES = "WHERE c.relkind = 'r' AND n.nspname = ANY (CURRENT_SCHEMAS(false))";

    private static final String SQL_TABLE_CREATE = "create table \"%s\" (id bigserial primary key, key jsonb, properties jsonb)";

    private ConnectionManager initConnectionManager = new ConnectionManager(DataSourceInfo.INIT_DATASOURCE);

    private ConnectionManager connectionManager = new ConnectionManager();

    public void drop() {
        if (!databaseExists()) {
            return;
        }

        final String database = currentDatabase();

        initConnectionManager.execute(new SqlRunner(SQL_DATABASE_KILL_ACTIVITY) {
            @Override
            protected void bind() {
                bind("database", database);
            }
        });

        initConnectionManager.execute(String.format(SQL_DATABASE_DROP, database));
    }

    public void create() {
        if (databaseExists()) {
            return;
        }

        initConnectionManager.execute(new SqlRunner(SQL_DATABASE_KILL_ACTIVITY) {
            @Override
            protected void bind() {
                bind("database", initDatabase());
            }
        });

        initConnectionManager.execute(String.format(SQL_DATABASE_CREATE, currentDatabase()));

    }

    public boolean databaseExists() {
        final String database = currentDatabase();

        Boolean exists = initConnectionManager.executeQuery(new SqlRunner(SQL_DATABASE_EXISTS) {
            @Override
            protected void bind() {
                bind("database", database);
            }

            @Override
            protected Boolean collectSingle(ResultSet rs) throws SQLException {
                return Boolean.TRUE;
            }
        });
        return exists != null && exists;
    }

    public void sync(Set<Class<?>> endpointClazzes) {
        create();

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
        connectionManager.execute(String.format(SQL_TABLE_CREATE, kind));
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

    private String currentDatabase() {
        JettyConfiguration config = JettyConfiguration.get();
        return config.getDatasourceInfo().getDatabaseName();
    }

    private String initDatabase() {
        JettyConfiguration config = JettyConfiguration.get();
        return config.getDatasourceInfo().getInitDatasource().getDatabaseName();
    }
}
