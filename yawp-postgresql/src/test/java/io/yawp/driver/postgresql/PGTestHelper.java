package io.yawp.driver.postgresql;

import io.yawp.driver.api.TestHelper;
import io.yawp.driver.postgresql.datastore.InitialContextMock;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;
import io.yawp.repository.Repository;

public class PGTestHelper implements TestHelper {

	private Repository r;

	@Override
	public void init(Repository r) {
		this.r = r;
	}

	@Override
	public void setUp() {
		InitialContextMock.configure();
		syncTables();
	}

	private void syncTables() {
		//SchemaSynchronizer.sync(r.getFeatures().getEndpointClazzes());

	}

	@Override
	public void tearDown() {
	}

}
