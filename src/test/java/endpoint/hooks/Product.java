package endpoint.hooks;

import endpoint.DatastoreObject;

public class Product extends DatastoreObject {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
