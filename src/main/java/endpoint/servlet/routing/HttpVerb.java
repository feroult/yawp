package endpoint.servlet.routing;

import endpoint.servlet.HttpException;

public enum HttpVerb {

	GET("get", RestActionType.SHOW), POST("post", RestActionType.CREATE), PUT("put", RestActionType.UPDATE), PATCH("patch",
			RestActionType.UPDATE), DELETE("delete", RestActionType.DELETE);

	private String name;
	private RestActionType defaultType;

	private HttpVerb(String name, RestActionType defaultType) {
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
