package endpoint.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import endpoint.repository.RepositoryFeatures;
import endpoint.repository.actions.ActionKey;
import endpoint.utils.HttpVerb;
import endpoint.utils.UriUtils;

public class UriParser {

	private String uri;

	private RepositoryFeatures features;

	private List<RouteResource> resources;

	private boolean overCollection;

	private ActionKey customActionKey;

	private HttpVerb verb;

	public UriParser(HttpVerb verb, String uri, RepositoryFeatures features) {
		this.verb = verb;
		this.uri = uri;
		this.features = features;
		parseUri();
	}

	public static UriParser parse(HttpVerb verb, String uri, RepositoryFeatures features) {
		return new UriParser(verb, uri, features);
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	private void parseUri() {
		String[] parts = UriUtils.normalizeUri(uri).split("/");

		this.customActionKey = parseCustomActionKey(parts);
		this.overCollection = checkIsOverCollection(parts);
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
		if (features.hasCustomAction(possiblePath, actionKey)) {
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
		if (features.hasCustomAction(possiblePath, actionKey)) {
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

	private boolean checkIsOverCollection(String[] parts) {
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
}
