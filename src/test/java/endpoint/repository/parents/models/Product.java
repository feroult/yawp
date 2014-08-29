package endpoint.repository.parents.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;

@Endpoint(path = "products")
public class Product {

	private IdRef<Product> id;
	private String name;
	private float price;

	@SuppressWarnings("unused")
	private Product() {
	}

	public Product(String name, float price) {
		this.name = name;
		this.price = price;
	}

	public IdRef<Product> getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public float getPrice() {
		return price;
	}
}
