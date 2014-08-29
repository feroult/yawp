package endpoint.servlet;

import endpoint.repository.IdRef;
import endpoint.repository.Repository;

public class RouteResource {

	private String endpointPath;

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
		this.endpointPath = "/" + endpoint;
		this.id = id;
	}

	public String getEndpointPath() {
		return endpointPath;
	}

	public IdRef<?> getIdRef(Repository r, IdRef<?> parent) {
		if (id == null) {
			return parent;
		}
		IdRef<?> idRef = IdRef.create(r, r.getEndpointFeatures(endpointPath).getClazz(), id);
		idRef.setParentId(parent);
		return idRef;
	}

	public Long getId() {
		return id;
	}
}
