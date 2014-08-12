package endpoint.hooks;

import endpoint.Target;
import endpoint.query.DatastoreQuery;

@Target(Product.class)
public class BeforeQueryHook extends Hook {

	public void beforeQuery(DatastoreQuery<Product> q) {
		q.where("name", "=", "xpto");
	}

}
