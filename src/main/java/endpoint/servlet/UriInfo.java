package endpoint.servlet;

import java.util.ArrayList;
import java.util.List;

import endpoint.utils.UriUtils;

public class UriInfo {

	private String uri;

	private List<RouteResource> resources;

	private boolean overCollection;

	public UriInfo(String uri) {
		this.uri = uri;
		parseUri();
	}

	public static UriInfo parse(String uri) {
		return new UriInfo(uri);
	}

	public List<RouteResource> getResources() {
		return resources;
	}

	private void parseUri() {
		String[] parts = UriUtils.normalizeUri(uri).split("/");

		this.overCollection = checkIsOverCollection(parts);
		this.resources = parseResources(parts);
	}

	private ArrayList<RouteResource> parseResources(String[] parts) {
		ArrayList<RouteResource> resources = new ArrayList<RouteResource>();

		if (parts.length == 1) {
			resources.add(new RouteResource(parts[0]));
		} else if (parts.length == 2) {
			resources.add(new RouteResource(parts[0], parts[1]));
		}

		// for (int i = 0; i < parts.length - 2; i += 2) {
		// resources.add(new RouteResource(parts[i], parts[i + 1]));
		// }
		//
		// // simpleobjects/123/action
		// if (parts.length % 2 == 1) {
		// resources.add(new RouteResource(parts[0]));
		// return;
		// }
		return resources;
	}

	private boolean checkIsOverCollection(String[] parts) {
		String lastToken = parts[parts.length - 1];
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
		return false;
	}

}
