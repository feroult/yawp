package endpoint.utils;

import com.google.appengine.api.utils.SystemProperty;

public class Environment {

	public static boolean isProduction() {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}

	public static boolean isDevelopment() {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
	}

	public static boolean isTest() {
		return !isProduction() && !isDevelopment();
	}
}