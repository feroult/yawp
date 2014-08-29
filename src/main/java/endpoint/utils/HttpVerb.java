package endpoint.utils;

import endpoint.servlet.HttpException;
import endpoint.servlet.RestActionType;

public enum HttpVerb {

	GET("get"), POST("post"), PUT("put"), PATCH("patch"), DELETE("delete");

	private String name;

	private RestActionType defaultType;

	private HttpVerb(String name) {
		this.name = name;
		this.defaultType = defaultType;
	}

	public RestActionType getActionType() {
		return defaultType;
	}

	public static HttpVerb getFromString(String method) {
		String methodLowerCase = method.toLowerCase();
		for (HttpVerb verb : HttpVerb.values()) {
			if (methodLowerCase.equals(verb.name)) {
				return verb;
			}
		}
		throw new HttpException(501, "Unsupported http verb " + method);
	}

	public String toString() {
		return this.name;
	}
}
