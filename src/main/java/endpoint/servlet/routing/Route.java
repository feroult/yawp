package endpoint.servlet.routing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import endpoint.repository.EndpointFeatures;
import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeatures;
import endpoint.repository.actions.ActionRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.servlet.HttpException;

public class Route {

	private List<RouteResource> resources;
	private RouteAction action;

	private Route(RepositoryFeatures features, List<RouteResource> resources, RouteAction action) {
		this.resources = resources;
		this.action = action;
		validateConstraints(features, resources, action);
	}

	private void validateConstraints(RepositoryFeatures features, List<RouteResource> resources, RouteAction action) {
		Endpoint lastEndpoint = features.getEndpoint(getLastEndpoint(resources).getEndpoint()).getEndpointAnnotation();
		if (!lastEndpoint.index() && action.getActionType() == RestActionType.INDEX) {
			throw new HttpException(403);
		}
		if (!lastEndpoint.update() && action.getActionType() == RestActionType.UPDATE) {
			throw new HttpException(403);
		}
	}

	public static String normalizeUri(String uri) {
		return normalizeUriEnd(normalizeUriStart(uri));
	}

	private static String normalizeUriStart(String uri) {
		if (uri.charAt(0) == '/') {
			return uri.substring(1);
		}
		return uri;
	}

	private static String normalizeUriEnd(String uri) {
		if (uri.charAt(uri.length() - 1) == '/') {
			return uri.substring(0, uri.length() - 1);
		}
		return uri;
	}

	// TODO validate if the resources chain is a valid structure given the
	// endpoints' parents, and give 404's if invalid
	public static Route generateRouteFor(RepositoryFeatures features, HttpVerb method, String uri) {
		String[] parts = normalizeUri(uri).split("/");
		List<RouteResource> resources = new ArrayList<>();
		RouteAction action;
		if (parts.length == 1) {
			resources.add(new RouteResource(parts[0]));
			if (method == HttpVerb.GET) {
				action = new RouteAction(RestActionType.INDEX);
			} else if (method == HttpVerb.POST) {
				action = new RouteAction(RestActionType.CREATE);
			} else {
				throw new HttpException(501, "Currently only GET and POST are support for collections, tryed to " + method);
			}
			return new Route(features, resources, action);
		}

		for (int i = 0; i < parts.length - 2; i += 2) {
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
				ActionRef ref = new ActionRef(method, lastToken, true);
				Method actionMethod = getActionMethod(features, resources, ref);
				if (actionMethod == null) {
					throw new HttpException(404);
				}
				action = new RouteAction(actionMethod);
			}
		} else {
			ActionRef ref = new ActionRef(method, lastToken, false);
			Method actionMethod = getActionMethod(features, resources, ref);
			if (actionMethod != null) {
				action = new RouteAction(actionMethod);
			} else {
				resources.add(new RouteResource(parts[parts.length - 1]));
				if (method == HttpVerb.GET) {
					action = new RouteAction(RestActionType.INDEX);
				} else if (method == HttpVerb.POST) {
					action = new RouteAction(RestActionType.CREATE);
				} else {
					throw new HttpException(501, "Currently only GET and POST are support for collections, tryed to " + method);
				}
			}
		}

		return new Route(features, resources, action);
	}

	private static Method getActionMethod(RepositoryFeatures features, List<RouteResource> resources, ActionRef action) {
		EndpointFeatures<?> endpointRef = features.getEndpoint(getLastEndpoint(resources).getEndpoint());
		Method actionMethod = endpointRef.getAction(action);
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

	public RestActionType getActionType() {
		return action.getActionType();
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	public EndpointFeatures<?> getLastEndpoint(RepositoryFeatures rf) {
		return rf.getEndpoint(getLastEndpoint(resources).getEndpoint());
	}

	public Method getCustomAction() {
		return action.getCustomAction();
	}

	public static boolean isValidResourceName(String endpointName) {
		for (char c : endpointName.toCharArray()) {
			if (!Character.isAlphabetic(c)) {
				return false;
			}
		}
		return true;
	}
}
