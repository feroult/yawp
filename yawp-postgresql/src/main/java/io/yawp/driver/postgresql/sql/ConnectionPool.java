package io.yawp.driver.postgresql.sql;

import io.yawp.commons.utils.Environment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final String JDBC_YAWP_PREFIX = "jdbc/yawp_";

    private String dataSourceName;

    public ConnectionPool() {
        this.dataSourceName = JDBC_YAWP_PREFIX + Environment.getOrDefault();
    }

    public ConnectionPool(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    protected Connection connection() {
        return connection(true);
    }

    protected Connection connection(boolean autoCommit) {
        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            DataSource ds = (DataSource) ctx.lookup(dataSourceName);
            Connection connection = ds.getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException | NamingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void rollbackAndClose(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection);
        }
    }

    protected void commitAndClose(Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(connection);
        }
    }

}
