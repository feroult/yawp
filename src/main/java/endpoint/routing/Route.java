package endpoint.routing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import endpoint.EndpointRef;
import endpoint.HttpException;
import endpoint.IdRef;
import endpoint.Repository;
import endpoint.RepositoryFeatures;
import endpoint.actions.ActionType;
import endpoint.annotations.Endpoint;

public class Route {

	private List<RouteResource> resources;
	private RouteAction action;

	public Route(List<RouteResource> resources, RouteAction action) {
		this.resources = resources;
		this.action = action;
	}

	public static Route generateRouteFor(RepositoryFeatures features, HttpVerb method, String uri) {
		String[] parts = uri.split("////");
		List<RouteResource> resources = new ArrayList<>();
		RouteAction action;
		if (parts.length == 1) {
			resources.add(new RouteResource(parts[0]));
			assertGet(method);
			action = new RouteAction(ActionType.INDEX);
			return new Route(resources, action);
		}

		for (int i = 0; i < parts.length - 1; i += 2) {
			resources.add(new RouteResource(parts[i], parts[i + 1]));
		}

		String lastToken = parts[parts.length - 1];
		if (parts.length % 2 == 0) {
			try {
				Long id = Long.parseLong(lastToken);
				resources.add(new RouteResource(parts[parts.length - 2], id));
				action = new RouteAction(method.getActionType());
			} catch (NumberFormatException e) {
				resources.add(new RouteResource(parts[parts.length - 2]));
				Method actionMethod = getActionMethod(features, resources, lastToken);
				if (actionMethod == null) {
					throw new HttpException(404);
				}
				action = new RouteAction(actionMethod);
			}
		} else {
			assertGet(method);
			Method actionMethod = getActionMethod(features, resources, lastToken);
			if (actionMethod != null) {
				action = new RouteAction(actionMethod);
			} else {
				resources.add(new RouteResource(parts[parts.length - 1]));
				action = new RouteAction(ActionType.INDEX);
			}
		}

		Endpoint lastEndpoint = features.getEndpoint(getLastEndpoint(resources).getEndpoint()).getEndpointAnnotation();
		if (!lastEndpoint.index() && action.getActionType() == ActionType.INDEX) {
			throw new HttpException(403);
		}
		if (!lastEndpoint.update() && action.getActionType() == ActionType.UPDATE) {
			throw new HttpException(403);
		}
		return new Route(resources, action);
	}

	private static Method getActionMethod(RepositoryFeatures features, List<RouteResource> resources, String lastToken) {
		EndpointRef<?> endpointRef = features.getEndpoint(getLastEndpoint(resources).getEndpoint());
		Method actionMethod = endpointRef.getAction(lastToken);
		return actionMethod;
	}

	private static RouteResource getLastEndpoint(List<RouteResource> resources) {
		return resources.get(resources.size() - 1);
	}

	public IdRef<?> getIdRef(Repository r) {
		IdRef<?> idRef = null;
		for (RouteResource resource : resources) {
			idRef = resource.getResourceId(r, idRef);
		}
		return idRef;
	}
	
	public ActionType getActionType() {
		return action.getActionType();
	}

	public List<RouteResource> getResources() {
		return resources;
	}
	
	private static void assertGet(HttpVerb method) {
		if (method != HttpVerb.GET) {
			throw new HttpException(501, "Currently only GET is support for collections");
		}
	}

	public EndpointRef<?> getLastEndpoint(RepositoryFeatures rf) {
		return rf.getEndpoint(getLastEndpoint(resources).getEndpoint());
	}

	public Method getCustomAction() {
		return action.getCustomAction();
	}
}
