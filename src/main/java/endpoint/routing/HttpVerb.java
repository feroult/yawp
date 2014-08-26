package endpoint.routing;

import endpoint.HttpException;
import endpoint.actions.ActionType;

public enum HttpVerb {

	GET("get", ActionType.SHOW), POST("post", ActionType.CREATE), PUT("put", ActionType.UPDATE), PATCH("patch", ActionType.UPDATE), DELETE("delete", ActionType.DELETE);

	private String name;
	private ActionType defaultType;

	private HttpVerb(String name, ActionType defaultType) {
		this.name = name;
		this.defaultType = defaultType;
	}
	
	public ActionType getActionType() {
		return defaultType;
	}

	public static HttpVerb getFromString(String method) {
		for (HttpVerb verb : HttpVerb.values()) {
			if (method.toLowerCase().equals(verb.name)) {
				return verb;
			}
		}
		throw new HttpException(501, "Unsupported http verb " + method);
	}
}
