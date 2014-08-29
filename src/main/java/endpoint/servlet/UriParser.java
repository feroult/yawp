package endpoint.servlet;

import java.util.ArrayList;
import java.util.List;

import endpoint.utils.UriUtils;

public class UriParser {

	private String uri;

	public UriParser(String uri) {
		this.uri = uri;
	}

	public static UriParser parse(String uri) {
		return new UriParser(uri);
	}

	public List<RouteResource> getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	protected static List<RouteResource> Xparse(String uri) {
		String[] parts = UriUtils.normalizeUri(uri).split("/");

		List<RouteResource> resources = new ArrayList<RouteResource>();

		// /simpleobjects
		if (parts.length == 1) {
			resources.add(new RouteResource(parts[0]));
			return resources;
		}

		for (int i = 0; i < parts.length - 2; i += 2) {
			resources.add(new RouteResource(parts[i], parts[i + 1]));
		}

		// simpleobjects/123/action
		if (parts.length % 2 == 1) {
			resources.add(new RouteResource(parts[0]));
			return resources;
		}



		return resources;
	}


}
