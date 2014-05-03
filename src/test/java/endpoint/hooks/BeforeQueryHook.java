package endpoint.hooks;

import endpoint.DatastoreQuery;
import endpoint.Target;

@Target(Product.class)
public class BeforeQueryHook extends Hook {

	public void beforeQuery(DatastoreQuery<Product> q) {
		q.where("name", "=", "xpto");
	}

}
