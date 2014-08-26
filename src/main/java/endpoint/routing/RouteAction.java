package endpoint.routing;

import java.lang.reflect.Method;

import endpoint.actions.ActionType;

public class RouteAction {

	private ActionType actionType;
	private Method customAction;
	
	public RouteAction(ActionType actionType) {
		this(actionType, null);
	}
	
	public RouteAction(Method customAction) {
		this(ActionType.CUSTOM, customAction);
	}

	private RouteAction(ActionType actionType, Method customAction) {
		this.actionType = actionType;
		this.customAction = customAction;
	}
	
	public ActionType getActionType() {
		return actionType;
	}
	
	public Method getCustomAction() {
		return customAction;
	}
}
