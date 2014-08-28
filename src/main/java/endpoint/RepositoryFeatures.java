package endpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import endpoint.routing.Route;
import endpoint.utils.EntityUtils;

public class RepositoryFeatures {

	private Map<Class<?>, EndpointRef<?>> endpoints;
	private Map<String, Class<?>> paths;

	public RepositoryFeatures(Collection<EndpointRef<?>> endpoints) {
		this.endpoints = new HashMap<>();
		this.paths = new HashMap<>();
		for (EndpointRef<?> endpoint : endpoints) {
			this.endpoints.put(endpoint.getClazz(), endpoint);
			String path = EntityUtils.getEndpointName(endpoint.getClazz());
			if (!path.isEmpty()) {
				String endpointName = Route.normalizeUri(path);
				if (paths.get(endpointName) != null) {
					throw new RuntimeException("Repeated endpoint name " + endpointName + " for class " + endpoint.getClazz().getSimpleName() + " (already found in class " + paths.get(endpointName).getSimpleName() + ")");
				}
				if (!Route.isValidResourceName(endpointName)) {
					throw new RuntimeException("Invalid endpoint name " + endpointName + " for class " + endpoint.getClazz().getSimpleName());
				}
				paths.put(endpointName, endpoint.getClazz());
			}
		}
		
	}
	
	public EndpointRef<?> getEndpoint(Class<?> clazz) {
		return endpoints.get(clazz);
	}

	public EndpointRef<?> getEndpoint(String path) {
		Class<?> clazz = paths.get(path);
		if (clazz == null) {
			throw new RuntimeException("Invalid endpoint path " + path);
		}
		return getEndpoint(clazz);
	}
}
