package endpoint.hooks;

import endpoint.query.DatastoreQuery;

public class BeforeQueryHook extends Hook<Product> {

	@Override
	public void beforeQuery(DatastoreQuery<Product> q) {
		q.where("name", "=", "xpto");
	}

}
