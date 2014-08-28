package endpoint.parents.models;

import java.util.List;

import endpoint.IdRef;
import endpoint.annotations.Endpoint;
import endpoint.annotations.Id;
import endpoint.annotations.Parent;

@Endpoint(path = "orders")
public class Order {

	@Id
	private IdRef<Order> id;
	@Parent
	private IdRef<Person> person;
	private List<IdRef<Product>> products;

	@SuppressWarnings("unused")
	private Order() { }

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
