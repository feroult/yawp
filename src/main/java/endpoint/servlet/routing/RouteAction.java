package endpoint.servlet.routing;

import java.lang.reflect.Method;

public class RouteAction {

	private RestActionType actionType;

	private Method customAction;

	public RouteAction(RestActionType actionType) {
		this(actionType, null);
	}

	public RouteAction(Method customAction) {
		this(RestActionType.CUSTOM, customAction);
	}

	private RouteAction(RestActionType actionType, Method customAction) {
		this.actionType = actionType;
		this.customAction = customAction;
	}

	public RestActionType getActionType() {
		return actionType;
	}

	public Method getCustomAction() {
		return customAction;
	}
}
