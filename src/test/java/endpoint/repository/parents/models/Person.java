package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Index;

@Endpoint(path = "/people")
public class Person {

	@Id
	private IdRef<Person> id;

	@Index
	private String name;

	private int age;

	@SuppressWarnings("unused")
	private Person() {
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public IdRef<Person> getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
