package io.yawp.commons.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

public abstract class FacadeUtils {

	private enum FacadeType {
		SET, GET
	}

	public static <I, T extends I> void get(T object, Class<I> facade) {
		hideProperties(object, notReadable(object.getClass(), facade));
	}

	public static <I, T extends I> void set(T object, T defaults, Class<I> facade) {
		assertSameClazz(object, defaults);
		copyProperties(defaults, object, notWriteable(object.getClass(), facade));
	}

	private static void assertSameClazz(Object object, Object defaults) {
		if (!object.getClass().equals(defaults.getClass())) {
			throw new RuntimeException("Objects must have the same class: " + object.getClass().getName() + " != "
					+ defaults.getClass().getName());
		}
	}

	private static void hideProperties(Object object, List<String> properties) {
		Class<?> clazz = object.getClass();
		try {
			for (String property : properties) {
				Field field = clazz.getDeclaredField(property);
				field.setAccessible(true);
				field.set(object, null);
			}
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static void copyProperties(Object from, Object to, List<String> properties) {
		try {
			for (String property : properties) {
				BeanUtils.copyProperty(to, property, BeanUtils.getProperty(from, property));
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<String> notReadable(Class<?> clazz, Class<?> facade) {
		return propetiesNotInFacade(clazz, facade, FacadeType.GET);
	}

	private static List<String> notWriteable(Class<?> clazz, Class<?> facade) {
		return propetiesNotInFacade(clazz, facade, FacadeType.SET);
	}

	private static List<String> facadeProperties(Class<?> facade, FacadeType type) {
		List<String> properties = new ArrayList<String>();

		for (Method attribute : facade.getMethods()) {
			String name = extractNameFromAttribute(facade, attribute, type);
			if (name == null) {
				continue;
			}
			properties.add(name);
		}

		return properties;
	}

	private static List<String> propetiesNotInFacade(Class<?> clazz, Class<?> facade, FacadeType facadeType) {
		List<String> properties = new ArrayList<String>();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
		List<String> facadeProperties = facadeProperties(facade, facadeType);
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String name = descriptor.getName();
			if (name.equals("class") || facadeProperties.contains(name)) {
				continue;
			}
			properties.add(name);
		}
		return properties;
	}

	private static String extractNameFromAttribute(Class<?> facade, Method attribute, FacadeType type) {
		String typePrefix = type.toString().toLowerCase();
		String methodName = attribute.getName();
		if (!methodName.startsWith(typePrefix)) {
			return null;
		}
		String capitalized = methodName.substring(typePrefix.length());
		return Character.toLowerCase(capitalized.charAt(0)) + capitalized.substring(1);
	}

}
