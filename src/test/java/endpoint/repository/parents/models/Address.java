package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Parent;

@Endpoint(path = "/addresses")
public class Address {

	@Id
	private IdRef<Address> id;

	private String street;

	private int number;

	private String city;

	@Parent
	private IdRef<Person> owner;

	@SuppressWarnings("unused")
	private Address() {
	}

	public Address(String street, int number, String city, IdRef<Person> owner) {
		this.street = street;
		this.number = number;
		this.city = city;
		this.owner = owner;
	}

	public IdRef<Address> getId() {
		return id;
	}

	public String getStreet() {
		return street;
	}

	public int getNumber() {
		return number;
	}

	public String getCity() {
		return city;
	}

	public IdRef<Person> getOwner() {
		return owner;
	}

	@Override
	public String toString() {
		return this.street + ", " + this.number + " - " + this.city;
	}
}
