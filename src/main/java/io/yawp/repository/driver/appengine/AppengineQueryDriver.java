package io.yawp.repository.driver.appengine;

import io.yawp.repository.Repository;
import io.yawp.repository.driver.api.QueryDriver;
import io.yawp.repository.query.DatastoreQuery;

import java.util.List;

public class AppengineQueryDriver implements QueryDriver {

	private Repository r;

	public AppengineQueryDriver(Repository r) {
		this.r = r;
	}

	@Override
	public <T> List<T> execute(DatastoreQuery<T> builder) {
		// TODO Auto-generated method stub
		return null;
	}

}
