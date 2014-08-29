package endpoint.repository.parents.models;

import java.util.List;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Parent;

@Endpoint(path = "/orders")
public class Order {

	@Id
	private IdRef<Order> id;
	@Parent
	private IdRef<Person> person;
	private List<IdRef<Product>> products;

	@SuppressWarnings("unused")
	private Order() {
	}

	public Order(IdRef<Order> id, IdRef<Person> person, List<IdRef<Product>> products) {
		this.id = id;
		this.person = person;
		this.products = products;
	}

	public IdRef<Order> getId() {
		return id;
	}

	public IdRef<Person> getPerson() {
		return person;
	}

	public List<IdRef<Product>> getProducts() {
		return products;
	}
}
