package io.yawp.utils;

public enum HttpVerb {

	GET, POST, PUT, PATCH, DELETE, OPTIONS;

	public static HttpVerb fromString(String method) {
		String methodLowerCase = method.toUpperCase();
		return valueOf(methodLowerCase);
	}

}
