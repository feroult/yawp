package endpoint.repository.hooks;

import endpoint.repository.Feature;
import endpoint.repository.query.DatastoreQuery;

public class Hook<T> extends Feature {

	public void beforeSave(T object) {
	}

	public void afterSave(T object) {
	}

	public void beforeQuery(DatastoreQuery<T> q) {
	}
}
