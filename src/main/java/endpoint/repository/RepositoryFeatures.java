package endpoint.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import endpoint.utils.HttpVerb;

public class RepositoryFeatures {

	private Map<Class<?>, EndpointFeatures<?>> endpoints;

	private Map<String, Class<?>> paths;

	public RepositoryFeatures(Collection<EndpointFeatures<?>> endpoints) {
		this.endpoints = new HashMap<>();
		this.paths = new HashMap<>();
		for (EndpointFeatures<?> endpoint : endpoints) {
			this.endpoints.put(endpoint.getClazz(), endpoint);
			String endpointPath = endpoint.getEndpointPath();
			if (!endpointPath.isEmpty()) {
				if (paths.get(endpointPath) != null) {
					throw new RuntimeException("Repeated endpoint path " + endpointPath + " for class "
							+ endpoint.getClazz().getSimpleName() + " (already found in class " + paths.get(endpointPath).getSimpleName()
							+ ")");
				}
				if (!isValidEndpointPath(endpointPath)) {
					throw new RuntimeException("Invalid endpoint path " + endpointPath + " for class "
							+ endpoint.getClazz().getSimpleName());
				}
				paths.put(endpointPath, endpoint.getClazz());
			}
		}

	}

	private boolean isValidEndpointPath(String endpointName) {
		char[] charArray = endpointName.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (i == 0) {
				if (c != '/') {
					return false;
				}
				continue;
			}
			if (!Character.isAlphabetic(c)) {
				return false;
			}
		}
		return true;
	}

	public EndpointFeatures<?> get(Class<?> clazz) {
		return endpoints.get(clazz);
	}

	public EndpointFeatures<?> get(String path) {
		Class<?> clazz = paths.get(path);
		if (clazz == null) {
			throw new RuntimeException("Invalid endpoint path " + path);
		}
		return get(clazz);
	}

	public boolean hasCustomAction(String path, HttpVerb verb, String customAction, boolean overCollection) {
		EndpointFeatures<?> endpointFeatures = get(path);
		return endpointFeatures.hasCustomAction(verb, customAction, overCollection);
	}
}
