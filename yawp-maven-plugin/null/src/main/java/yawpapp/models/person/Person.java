package yawpapp.models.person;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/people")
public class Person {

	@Id
	private IdRef<Person> id;

	public IdRef<Person> getId() {
		return id;
	}

	public void setId(IdRef<Person> id) {
		this.id = id;
	}

}
