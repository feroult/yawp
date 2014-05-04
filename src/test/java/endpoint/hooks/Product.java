package endpoint.hooks;

import endpoint.DatastoreObject;
import endpoint.Index;

public class Product extends DatastoreObject {

	@Index(normalize = true)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
