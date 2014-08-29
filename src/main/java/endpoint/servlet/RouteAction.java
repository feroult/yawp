package endpoint.servlet;

import java.lang.reflect.Method;

public class RouteAction {

	private RestAction actionType;

	private Method customAction;

	public RouteAction(RestAction actionType) {
		this(actionType, null);
	}

	public RouteAction(Method customAction) {
		this(RestAction.CUSTOM, customAction);
	}

	private RouteAction(RestAction actionType, Method customAction) {
		this.actionType = actionType;
		this.customAction = customAction;
	}

	public RestAction getActionType() {
		return actionType;
	}

	public Method getCustomAction() {
		return customAction;
	}
}
