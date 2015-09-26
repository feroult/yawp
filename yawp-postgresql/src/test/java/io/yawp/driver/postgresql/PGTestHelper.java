package io.yawp.driver.postgresql;

import java.sql.Connection;

import io.yawp.driver.api.TestHelper;
import io.yawp.driver.postgresql.datastore.ConnectionPool;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;
import io.yawp.driver.postgresql.datastore.sql.SqlRunner;
import io.yawp.repository.Repository;

public class PGTestHelper implements TestHelper {

	private Repository r;

	@Override
	public void init(Repository r) {
		this.r = r;
	}

	@Override
	public void setUp() {
		dropTables();
		createTables();
	}

	private void createTables() {
		SchemaSynchronizer.sync(r.getFeatures().getEndpointClazzes());

	}

	private void dropTables() {
		Connection connection = ConnectionPool.connection();
		try {
			new SqlRunner(connection, "drop schema public cascade; create schema public;").execute();
		} finally {
			ConnectionPool.close(connection);
		}
	}

	@Override
	public void tearDown() {
	}

}
