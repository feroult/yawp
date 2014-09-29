package io.yawp.repository;

import io.yawp.repository.query.DatastoreQuery;

public class Feature {

	protected Repository yawp;

	public void setRepository(Repository yawp) {
		this.yawp = yawp;
	}

	public <T> DatastoreQuery<T> yawp(Class<T> clazz) {
		return yawp.query(clazz);
	}

	public <T> DatastoreQuery<T> yawpWithHooks(Class<T> clazz) {
		return yawp.queryWithHooks(clazz);
	}

	public <T extends Feature> T feature(Class<T> clazz) {
		try {
			T feature = clazz.newInstance();
			feature.setRepository(yawp);
			return feature;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
