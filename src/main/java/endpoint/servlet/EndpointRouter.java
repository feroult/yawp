package endpoint.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import endpoint.repository.EndpointFeatures;
import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeatures;
import endpoint.repository.actions.ActionKey;
import endpoint.repository.annotations.Endpoint;
import endpoint.utils.HttpVerb;
import endpoint.utils.UriUtils;

public class EndpointRouter {

	private Repository r;

	private List<RouteResource> resources;

	private RESTActionType restAction;

	private ActionKey customActionKey;

	private RouteAction action;

	private EndpointRouter(Repository r, List<RouteResource> resources, RouteAction action) {
		this.r = r;
		this.resources = resources;
		this.action = action;
		validateConstraints();
	}

	private void validateConstraints() {
		RepositoryFeatures features = r.getFeatures();
		Endpoint lastEndpoint = features.get(getLastEndpoint(resources).getEndpointPath()).getEndpointAnnotation();
		if (!lastEndpoint.index() && action.getActionType() == RESTActionType.INDEX) {
			throw new HttpException(403);
		}
		if (!lastEndpoint.update() && action.getActionType() == RESTActionType.UPDATE) {
			throw new HttpException(403);
		}
	}

	// TODO validate if the resources chain is a valid structure given the
	// endpoints' parents, and give 404's if invalid
	public static EndpointRouter generateRouteFor(Repository r, String method, String uri) {
		RepositoryFeatures features = r.getFeatures();
		HttpVerb httpVerb = HttpVerb.fromString(method);

		String[] parts = UriUtils.normalizeUri(uri).split("/");

		List<RouteResource> resources = new ArrayList<>();
		RouteAction action = null;

		// /simpleobjects
		if (parts.length == 1) {
			return generateRouteForRootCollection(r, httpVerb, parts);
		}

		for (int i = 0; i < parts.length - 2; i += 2) {
			resources.add(new RouteResource(parts[i], parts[i + 1]));
		}

		String lastToken = parts[parts.length - 1];

		if (parts.length % 2 == 0) {
			try {
				return generateRouteForNestedResource(r, httpVerb, parts, resources, lastToken);
			} catch (NumberFormatException e) {
				return generateRouteForCustomActionOverNestedCollection(r, features, httpVerb, parts, resources, lastToken);
			}
		} else {
			return generateRouteForCustomActionOverCollection(r, features, httpVerb, parts, resources, action, lastToken);
		}
	}

	private static EndpointRouter generateRouteForCustomActionOverCollection(Repository r, RepositoryFeatures features, HttpVerb httpVerb,
			String[] parts, List<RouteResource> resources, RouteAction action, String lastToken) {
		// custom action over resource
		ActionKey ref = new ActionKey(httpVerb, lastToken, false);
		Method actionMethod = getActionMethod(features, resources, ref);
		if (actionMethod != null) {
			action = new RouteAction(actionMethod);
		} else {
			resources.add(new RouteResource(parts[parts.length - 1]));
			if (httpVerb == HttpVerb.GET) {
				action = new RouteAction(RESTActionType.INDEX);
			} else if (httpVerb == HttpVerb.POST) {
				action = new RouteAction(RESTActionType.CREATE);
			} else {
				throw new HttpException(501, "Currently only GET and POST are support for collections, tryed to " + httpVerb);
			}
		}
		return new EndpointRouter(r, resources, action);
	}

	private static EndpointRouter generateRouteForCustomActionOverNestedCollection(Repository r, RepositoryFeatures features,
			HttpVerb httpVerb, String[] parts, List<RouteResource> resources, String lastToken) {
		RouteAction action;
		// custom action over collection
		// /simpleobjects/1/xpto/active

		resources.add(new RouteResource(parts[parts.length - 2]));
		ActionKey ref = new ActionKey(httpVerb, lastToken, true);
		Method actionMethod = getActionMethod(features, resources, ref);
		if (actionMethod == null) {
			throw new HttpException(404);
		}
		action = new RouteAction(actionMethod);

		return new EndpointRouter(r, resources, action);
	}

	private static EndpointRouter generateRouteForNestedResource(Repository r, HttpVerb httpVerb, String[] parts,
			List<RouteResource> resources, String lastToken) {
		RouteAction action;
		// nested resource route
		// /simpleobjects/1/xpto/10

		Long id = Long.parseLong(lastToken);
		resources.add(new RouteResource(parts[parts.length - 2], id));
		action = new RouteAction(RESTActionType.defaultRestAction(httpVerb, false));

		return new EndpointRouter(r, resources, action);
	}

	private static EndpointRouter generateRouteForRootCollection(Repository r, HttpVerb httpVerb, String[] parts) {
		List<RouteResource> resources = new ArrayList<>();
		RouteAction action;

		resources.add(new RouteResource(parts[0]));
		if (httpVerb == HttpVerb.GET) {
			action = new RouteAction(RESTActionType.INDEX);
		} else if (httpVerb == HttpVerb.POST) {
			action = new RouteAction(RESTActionType.CREATE);
		} else {
			throw new HttpException(501, "Currently only GET and POST are support for collections, tryed to " + httpVerb);
		}
		return new EndpointRouter(r, resources, action);
	}

	private static Method getActionMethod(RepositoryFeatures features, List<RouteResource> resources, ActionKey action) {
		EndpointFeatures<?> endpoint = features.get(getLastEndpoint(resources).getEndpointPath());
		Method actionMethod = endpoint.getAction(action);
		return actionMethod;
	}

	private static RouteResource getLastEndpoint(List<RouteResource> resources) {
		return resources.get(resources.size() - 1);
	}

	public IdRef<?> getIdRef() {
		IdRef<?> idRef = null;
		for (RouteResource resource : resources) {
			idRef = resource.getIdRef(r, idRef);
		}
		return idRef;
	}

	public RESTActionType getRestAction() {
		return action.getActionType();
	}

	public EndpointFeatures<?> getEndpoint() {
		RepositoryFeatures features = r.getFeatures();
		return features.get(getLastEndpoint(resources).getEndpointPath());
	}

	public Method getCustomAction() {
		return action.getCustomAction();
	}

}
