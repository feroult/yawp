package ${package}.models;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/people")
public class Person {

	@Id
	private IdRef<Person> id;

	private String name;

	public IdRef<Person> getId() {
		return id;
	}

	public void setId(IdRef<Person> id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
