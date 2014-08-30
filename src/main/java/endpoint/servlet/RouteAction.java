package endpoint.servlet;

import java.lang.reflect.Method;

public class RouteAction {

	private RESTActionType actionType;

	private Method customAction;

	public RouteAction(RESTActionType actionType) {
		this(actionType, null);
	}

	public RouteAction(Method customAction) {
		this(RESTActionType.CUSTOM, customAction);
	}

	private RouteAction(RESTActionType actionType, Method customAction) {
		this.actionType = actionType;
		this.customAction = customAction;
	}

	public RESTActionType getActionType() {
		return actionType;
	}

	public Method getCustomAction() {
		return customAction;
	}
}
