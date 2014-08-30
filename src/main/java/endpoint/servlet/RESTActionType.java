package endpoint.servlet;

import endpoint.repository.annotations.Endpoint;
import endpoint.utils.HttpVerb;

public enum RESTActionType {
	INDEX, SHOW, CREATE, UPDATE, DELETE, CUSTOM;

	public static RESTActionType defaultRestAction(HttpVerb verb, boolean overCollection) {
		switch (verb) {
		case GET:
			return overCollection ? INDEX : SHOW;
		case POST:
			return overCollection ? UPDATE : CREATE;
		case PUT:
		case PATCH:
			return UPDATE;
		case DELETE:
			return DELETE;
		}
		throw new RuntimeException("Invalid HttpVerb: " + verb);
	}

	public void validateRetrictions(Endpoint endpointAnnotation) {
		if (this == INDEX && !endpointAnnotation.index()) {
			throw new HttpException(403);
		}

		if (this == UPDATE && !endpointAnnotation.update()) {
			throw new HttpException(403);
		}
	}
}
