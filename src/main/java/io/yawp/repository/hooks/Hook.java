package io.yawp.repository.hooks;

import io.yawp.repository.Feature;
import io.yawp.repository.query.DatastoreQuery;

public class Hook<T> extends Feature {

	public void beforeSave(T object) {
	}

	public void afterSave(T object) {
	}

	public void beforeQuery(DatastoreQuery<T> q) {
	}
}
