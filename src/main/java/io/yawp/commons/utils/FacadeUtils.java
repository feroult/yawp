package io.yawp.commons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.BeanUtils;

public final class FacadeUtils {

	private FacadeUtils() {
		throw new RuntimeException("Should not be instanciated");
	}

	public static <T> void copy(T from, T to, Class<T> facade) {
		for (Method attribute : facade.getMethods()) {
			try {
				String name = extractNameFromAttribute(facade, attribute);
				BeanUtils.copyProperty(to, name, BeanUtils.getProperty(from, name));
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException("Unexpected exception", e);
			}
		}
	}

	private static String extractNameFromAttribute(Class<?> facade, Method attribute) {
		String methodName = attribute.getName();
		if (!methodName.startsWith("get")) {
			throw new RuntimeException("Class " + facade + " was used as a Façade, but method " + methodName + " is no getter.");
		}
		String capitalized = methodName.substring("get".length());
		return Character.toLowerCase(capitalized.charAt(0)) + capitalized.substring(1);
	}
}
