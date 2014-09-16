package io.yawp.servlet;

import io.yawp.repository.annotations.Endpoint;
import io.yawp.utils.HttpVerb;

public enum RESTActionType {
	INDEX, SHOW, CREATE, UPDATE, DESTROY, CUSTOM;

	public static RESTActionType defaultRestAction(HttpVerb verb, boolean overCollection) {
		switch (verb) {
		case GET:
			return overCollection ? INDEX : SHOW;
		case POST:
			return CREATE;
		case PUT:
		case PATCH:
			assertNotOverCollection(overCollection);
			return UPDATE;
		case DELETE:
			assertNotOverCollection(overCollection);
			return DESTROY;
		}
		throw new HttpException(501, "Unsuported http verb " + verb);
	}

	private static void assertNotOverCollection(boolean overCollection) {
		if (overCollection) {
			throw new HttpException(501);
		}
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
