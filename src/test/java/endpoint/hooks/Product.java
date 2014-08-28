package endpoint.hooks;

import endpoint.annotations.Endpoint;
import endpoint.annotations.Id;
import endpoint.annotations.Index;

@Endpoint
public class Product {

	@Id
	private Long id;

	@Index(normalize = true)
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
