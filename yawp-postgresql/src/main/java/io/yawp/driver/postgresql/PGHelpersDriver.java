package io.yawp.driver.postgresql;

import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.postgresql.datastore.SchemaSynchronizer;

import javax.servlet.Filter;

public class PGHelpersDriver implements HelpersDriver {

	@Override
	public void deleteAll() {
		SchemaSynchronizer.truncateAll();
	}

	@Override
	public Filter getDevServerFilter() {
		return null;
	}

}
