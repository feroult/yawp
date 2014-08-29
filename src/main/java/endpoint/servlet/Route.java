package endpoint.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import endpoint.repository.EndpointFeatures;
import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeaturesCache;
import endpoint.repository.actions.ActionRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.utils.HttpVerb;
import endpoint.utils.UriUtils;

public class Route {

	private RepositoryFeaturesCache features;

	private List<RouteResource> resources;

	private RouteAction action;

	private Route(RepositoryFeaturesCache features, List<RouteResource> resources, RouteAction action) {
		this.features = features;
		this.resources = resources;
		this.action = action;
		validateConstraints();
	}

	private void validateConstraints() {
		Endpoint lastEndpoint = features.getEndpoint(getLastEndpoint(resources).getEndpointPath()).getEndpointAnnotation();
		if (!lastEndpoint.index() && action.getActionType() == RestAction.INDEX) {
			throw new HttpException(403);
		}
		if (!lastEndpoint.update() && action.getActionType() == RestAction.UPDATE) {
			throw new HttpException(403);
		}
	}

	// TODO validate if the resources chain is a valid structure given the
	// endpoints' parents, and give 404's if invalid
	public static Route generateRouteFor(RepositoryFeaturesCache features, HttpVerb method, String uri) {
		String[] parts = UriUtils.normalizeUri(uri).split("/");
		List<RouteResource> resources = new ArrayList<>();
		RouteAction action;
		if (parts.length == 1) {
			resources.add(new RouteResource(parts[0]));
			if (method == HttpVerb.GET) {
				action = new RouteAction(RestAction.INDEX);
			} else if (method == HttpVerb.POST) {
				action = new RouteAction(RestAction.CREATE);
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
				action = new RouteAction(RestAction.getDefaultRestAction(method));
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
					action = new RouteAction(RestAction.INDEX);
				} else if (method == HttpVerb.POST) {
					action = new RouteAction(RestAction.CREATE);
				} else {
					throw new HttpException(501, "Currently only GET and POST are support for collections, tryed to " + method);
				}
			}
		}

		return new Route(features, resources, action);
	}

	private static Method getActionMethod(RepositoryFeaturesCache features, List<RouteResource> resources, ActionRef action) {
		EndpointFeatures<?> endpointRef = features.getEndpoint(getLastEndpoint(resources).getEndpointPath());
		Method actionMethod = endpointRef.getAction(action);
		return actionMethod;
	}

	private static RouteResource getLastEndpoint(List<RouteResource> resources) {
		return resources.get(resources.size() - 1);
	}

	public IdRef<?> getIdRef(Repository r) {
		IdRef<?> idRef = null;
		for (RouteResource resource : resources) {
			idRef = resource.getIdRef(r, idRef);
		}
		return idRef;
	}

	public RestAction getActionType() {
		return action.getActionType();
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	public EndpointFeatures<?> getLastEndpoint() {
		return features.getEndpoint(getLastEndpoint(resources).getEndpointPath());
	}

	public Method getCustomAction() {
		return action.getCustomAction();
	}

}
