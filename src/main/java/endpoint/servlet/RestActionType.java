package endpoint.servlet;

import endpoint.utils.HttpVerb;

public enum RestActionType {
	INDEX, SHOW, CREATE, UPDATE, DELETE, CUSTOM;

	public static RestActionType getRest(HttpVerb verb) {
		switch (verb) {
		case GET:
			return SHOW;
		case POST:
			return CREATE;
		case PUT:
		case PATCH:
			return UPDATE;
		case DELETE:
			return DELETE;
		}
		throw new RuntimeException("Invalid HttpVerb: " + verb);
	}
}
