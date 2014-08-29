package endpoint.servlet;

import java.util.ArrayList;
import java.util.List;

import endpoint.utils.UriUtils;

public class UriParser {

	private String uri;

	private List<RouteResource> resources;

	private boolean overCollection;

	public UriParser(String uri) {
		this.uri = uri;
		parseUri();
	}

	public static UriParser parse(String uri) {
		return new UriParser(uri);
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

		for (int i = 0; i < parts.length / 2; i++) {
			String path = parts[i * 2];
			String id = parts[i * 2 + 1];
			resources.add(new RouteResource(path, id));
		}

		if (parts.length % 2 == 1) {
			resources.add(new RouteResource(parts[parts.length - 1]));
		}

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
