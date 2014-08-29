package endpoint.repository.hooks;

import endpoint.repository.query.DatastoreQuery;

public class BeforeQueryHook extends Hook<Product> {

	@Override
	public void beforeQuery(DatastoreQuery<Product> q) {
		q.where("name", "=", "xpto");
	}

}
