package endpoint.hooks;

import endpoint.RepositoryFeature;
import endpoint.query.DatastoreQuery;

public class Hook<T> extends RepositoryFeature {

	public void beforeSave(T object) {
	}

	public void afterSave(T object) {
	}

	public void beforeQuery(DatastoreQuery<T> q) {
	}
}
