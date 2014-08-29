package endpoint.servlet;

import java.util.ArrayList;
import java.util.List;

import endpoint.repository.RepositoryFeatures;
import endpoint.utils.UriUtils;

public class UriParser {

	private String uri;

	private RepositoryFeatures features;

	private List<RouteResource> resources;

	private boolean overCollection;

	private boolean customAction;

	public UriParser(String uri, RepositoryFeatures features) {
		this.uri = uri;
		this.features = features;
		parseUri();
	}

	public static UriParser parse(String uri, RepositoryFeatures features) {
		return new UriParser(uri, features);
	}

	public static UriParser parse(String uri) {
		return parse(uri, null);
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	private void parseUri() {
		String[] parts = UriUtils.normalizeUri(uri).split("/");

		this.customAction = checkIsCustomAction(parts);
		this.overCollection = checkIsOverCollection(parts);
		this.resources = parseResources(parts);
	}

	private boolean checkIsCustomAction(String[] parts) {
		if (parts.length < 2) {
			return false;
		}

		// /objects/1
		String possibleAction = parts[parts.length - 1];
		if (!isString(possibleAction)) {
			return false;
		}

		// /objects/action
		if (parts.length % 2 == 0) {
			String possiblePath = parts[parts.length - 2];
			if (!isString(possiblePath)) {
				return false;
			}
			return features.hasCustomAction(possiblePath, null, possibleAction, true);
		}

		// /objects/1/action
		String possiblePath = parts[parts.length - 3];
		return features.hasCustomAction(possiblePath, null, possibleAction, false);
	}

	private ArrayList<RouteResource> parseResources(String[] parts) {
		ArrayList<RouteResource> resources = new ArrayList<RouteResource>();

		for (int i = 0; i < parts.length / 2; i++) {
			String path = parts[i * 2];
			String id = parts[i * 2 + 1];

			if (!customAction) {
				resources.add(new RouteResource(path, id));
			}
		}

		if (parts.length % 2 == 1) {
			resources.add(new RouteResource(parts[parts.length - 1]));
		}

		return resources;
	}

	private boolean checkIsOverCollection(String[] parts) {
		String lastToken = parts[parts.length - 1];
		return isString(lastToken);
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
		return customAction;
	}

	public String getCustomAction() {
		// TODO Auto-generated method stub
		return null;
	}
}
