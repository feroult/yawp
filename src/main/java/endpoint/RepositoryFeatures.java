package endpoint;

import java.util.Map;

public class RepositoryFeatures {

	private Map<String, EndpointRef<?>> endpoints;

	public RepositoryFeatures(Map<String, EndpointRef<?>> endpoints) {
		this.endpoints = endpoints;
	}
	
	public EndpointRef<?> getEndpoint(String name) {
		return endpoints.get(name);
	}
}
