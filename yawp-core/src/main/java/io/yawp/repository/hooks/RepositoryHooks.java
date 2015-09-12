package io.yawp.repository.hooks;

import io.yawp.commons.utils.ThrownExceptionsUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RepositoryHooks {

	public static void beforeSave(Repository r, Object object) {
		invokeHooks(r, object.getClass(), object, "beforeSave");
	}

	public static void afterSave(Repository r, Object object) {
		invokeHooks(r, object.getClass(), object, "afterSave");
	}

	public static <T> void beforeQuery(Repository r, QueryBuilder<T> q, Class<T> clazz) {
		invokeHooks(r, clazz, q, "beforeQuery");
	}

	public static void beforeDestroy(Repository r, IdRef<?> id) {
		invokeHooks(r, id.getClazz(), id, "beforeDestroy");
	}

	public static void afterDestroy(Repository r, IdRef<?> id) {
		invokeHooks(r, id.getClazz(), id, "afterDestroy");
	}

	private static void invokeHooks(Repository r, Class<?> targetClazz, Object argument, String methodName) {
		for (Class<? extends Hook<?>> hookClazz : r.getEndpointFeatures(targetClazz).getHooks()) {
			invokeHookMethod(r, hookClazz, methodName, argument);
		}
	}

	private static void invokeHookMethod(Repository r, Class<? extends Hook<?>> hookClazz, String methodName, Object argument) {
		try {
			Hook<?> hook = hookClazz.newInstance();
			hook.setRepository(r);

			Method hookMethod = getMethod(hook, methodName, argument.getClass());

			if (hookMethod == null) {
				hookMethod = getMethod(hook, methodName, Object.class);
			}

			if (hookMethod != null) {
				hookMethod.invoke(hook, argument);
			}
		} catch (InstantiationException ex) {
			throw new RuntimeException("The Hook class " + hookClazz.getSimpleName()
					+ " must have a default constructor, and it must not throw exceptions.", ex);
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
			throw ThrownExceptionsUtils.handle(ex);
		}
	}

	private static Method getMethod(Object hook, String methodName, Class<?> argumentClazz) {

		try {
			return hook.getClass().getMethod(methodName, argumentClazz);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}

	}
}
