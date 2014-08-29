package endpoint.utils;

public class UriUtils {
	public static String normalizeUri(String uri) {
		return normalizeUriEnd(normalizeUriStart(uri));
	}

	public static String normalizeUriStart(String uri) {
		if (uri.charAt(0) == '/') {
			return uri.substring(1);
		}
		return uri;
	}

	public static String normalizeUriEnd(String uri) {
		if (uri.charAt(uri.length() - 1) == '/') {
			return uri.substring(0, uri.length() - 1);
		}
		return uri;
	}
}
