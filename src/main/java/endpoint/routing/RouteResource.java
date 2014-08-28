package endpoint.routing;

import endpoint.HttpException;
import endpoint.IdRef;
import endpoint.Repository;

public class RouteResource {

	private String endpoint;

	private Long id;

	public RouteResource(String endpoint) {
		this(endpoint, (Long) null);
	}

	public RouteResource(String endpoint, String id) {
		this(endpoint, getAsLong(id));
	}

	private static Long getAsLong(String id) {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException ex) {
			throw new HttpException(404);
		}
	}

	public RouteResource(String endpoint, Long id) {
		this.endpoint = endpoint;
		this.id = id;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public Long getId() {
		return id;
	}

	public IdRef<?> getResourceId(Repository r, IdRef<?> parent) {
		if (id == null) {
			return parent;
		}
		IdRef<?> idRef = IdRef.create(r, r.getEndpointFeatures(endpoint).getClazz(), id);
		idRef.setParentId(parent);
		return idRef;
	}
}
