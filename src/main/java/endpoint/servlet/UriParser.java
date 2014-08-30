package endpoint.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import endpoint.repository.EndpointFeatures;
import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeatures;
import endpoint.repository.actions.ActionKey;
import endpoint.repository.annotations.Endpoint;
import endpoint.utils.HttpVerb;
import endpoint.utils.UriUtils;

public class UriParser {

	private Repository r;

	private RepositoryFeatures features;

	private String uri;

	private List<RouteResource> resources;

	private boolean overCollection;

	private ActionKey customActionKey;

	private HttpVerb verb;

	public UriParser(Repository r, HttpVerb verb, String uri) {
		this.verb = verb;
		this.uri = uri;
		this.r = r;
		this.features = r.getFeatures();
		parseUri();
		validateRestrictions();
	}

	public static UriParser parse(Repository r, HttpVerb verb, String uri) {
		return new UriParser(r, verb, uri);
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	private void parseUri() {
		String[] parts = UriUtils.normalizeUri(uri).split("/");

		this.customActionKey = parseCustomActionKey(parts);
		this.overCollection = parseOverCollection(parts);
		this.resources = parseResources(parts);
	}

	private ActionKey parseCustomActionKey(String[] parts) {
		// /objects
		if (parts.length < 2) {
			return null;
		}

		String possibleAction = parts[parts.length - 1];

		// /objects/1
		if (!isString(possibleAction)) {
			return null;
		}

		// /objects/action
		if (parts.length % 2 == 0) {
			return parseCustomActionKeyForEvenParts(parts, possibleAction);
		}

		// /objects/1/action
		return parseCustomActionKeyForOddParts(parts, possibleAction);
	}

	private ActionKey parseCustomActionKeyForOddParts(String[] parts, String possibleAction) {
		String possiblePath = parts[parts.length - 3];
		ActionKey actionKey = new ActionKey(verb, possibleAction, false);
		if (features.hasCustomAction("/" + possiblePath, actionKey)) {
			return actionKey;
		}
		return null;
	}

	private ActionKey parseCustomActionKeyForEvenParts(String[] parts, String possibleAction) {
		String possiblePath = parts[parts.length - 2];
		if (!isString(possiblePath)) {
			return null;
		}

		ActionKey actionKey = new ActionKey(verb, possibleAction, true);
		if (features.hasCustomAction("/" + possiblePath, actionKey)) {
			return actionKey;
		}
		return null;
	}

	private ArrayList<RouteResource> parseResources(String[] parts) {
		ArrayList<RouteResource> resources = new ArrayList<RouteResource>();

		String[] resourceParts = parts;
		if (isCustomAction()) {
			resourceParts = Arrays.copyOf(parts, parts.length - 1);
		}

		for (int i = 0; i < resourceParts.length / 2; i++) {
			String path = resourceParts[i * 2];
			String id = resourceParts[i * 2 + 1];
			resources.add(new RouteResource(path, id));
		}

		if (resourceParts.length % 2 == 1) {
			resources.add(new RouteResource(resourceParts[resourceParts.length - 1]));
		}

		return resources;
	}

	private boolean parseOverCollection(String[] parts) {
		if (parts.length == 1) {
			return true;
		}
		if (!isString(parts[parts.length - 1])) {
			return false;
		}
		if (!isCustomAction()) {
			return true;
		}
		return isString(parts[parts.length - 2]);
	}

	private boolean isString(String lastToken) {
		try {
			Long.valueOf(lastToken);
			return false;
		} catch (NumberFormatException e) {
			return true;
		}
	}

	public boolean isOverCollection() {
		return overCollection;
	}

	public boolean isCustomAction() {
		return customActionKey != null;
	}

	public String getCustomActionName() {
		if (!isCustomAction()) {
			return null;
		}
		return customActionKey.getActionName();
	}

	public ActionKey getCustomActionKey() {
		return customActionKey;
	}

	private String getEndpointPath() {
		return resources.get(resources.size() - 1).getEndpointPath();
	}

	public EndpointFeatures<?> getEndpointFeatures() {
		return features.get(getEndpointPath());
	}

	public Class<?> getEndpointClazz() {
		return getEndpointFeatures().getClazz();
	}

	public IdRef<?> getIdRef() {
		IdRef<?> idRef = null;
		for (RouteResource resource : resources) {
			idRef = resource.getIdRef(r, idRef);
		}
		return idRef;
	}

	public RESTActionType getRESTActionType() {
		if (isCustomAction()) {
			return RESTActionType.CUSTOM;
		}
		return RESTActionType.defaultRestAction(verb, isOverCollection());
	}

	private void validateRestrictions() {
		Endpoint endpointAnnotation = getEndpointFeatures().getEndpointAnnotation();
		getRESTActionType().validateRetrictions(endpointAnnotation);
	}

}
