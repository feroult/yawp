package io.yawp.repository;

public class EndpointNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6669941619903531041L;

	private String endpointPath;

	public EndpointNotFoundException(String endpointPath) {
		this.endpointPath = endpointPath;
	}

	public String getEndpointPath() {
		return endpointPath;
	}

}
