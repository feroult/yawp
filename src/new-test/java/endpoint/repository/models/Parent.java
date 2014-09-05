package endpoint.repository.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;

@Endpoint(path = "/parents")
public class Parent {

	@Id
	private IdRef<Parent> id;

	private String name;

	public IdRef<Parent> getId() {
		return id;
	}

	public void setId(IdRef<Parent> id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
