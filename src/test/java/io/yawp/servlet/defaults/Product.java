package io.yawp.servlet.defaults;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/products")
public class Product {

	@Id
	private IdRef<Product> id;

	private String name;

	public Product() {

	}

	public Product(String name) {
		this.name = name;
	}

	public IdRef<Product> getId() {
		return id;
	}

	public void setId(IdRef<Product> id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
