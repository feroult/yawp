package endpoint.repository.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import endpoint.repository.Repository;
import endpoint.repository.query.DatastoreQuery;
import endpoint.utils.ThrownExceptionsUtils;

public class RepositoryHooks {

	public static void beforeSave(Repository r, Object object) {
		invokeHooks(r, object.getClass(), object, "beforeSave");
	}

	public static void afterSave(Repository r, Object object) {
		invokeHooks(r, object.getClass(), object, "afterSave");
	}

	public static <T> void beforeQuery(Repository r, DatastoreQuery<T> q, Class<T> clazz) {
		invokeHooks(r, clazz, q, "beforeQuery");
	}

	private static void invokeHooks(Repository r, Class<?> targetClazz, Object object, String methodName) {
		for (Class<? extends Hook<?>> hookClazz : r.getEndpointFeatures(targetClazz).getHooks()) {
			invokeHookMethod(r, object, methodName, hookClazz);
		}
	}

	private static void invokeHookMethod(Repository r, Object object, String methodName, Class<? extends Hook<?>> hookClazz) {
		try {
			Hook<?> hook = hookClazz.newInstance();
			hook.setRepository(r);

			Method hookMethod = getMethod(hook, methodName, object.getClass());
			if (hookMethod != null) {
				hookMethod.invoke(hook, object);
			}
		} catch (InstantiationException ex) {
			throw new RuntimeException("The Hook class " + hookClazz.getSimpleName()
					+ " must have a default constructor, and it must not throw exceptions.", ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException("An exception has occured when running the " + methodName + " hook for class "
					+ hookClazz.getSimpleName(), ex);
		} catch (IllegalAccessException | IllegalArgumentException ex) {
			throw ThrownExceptionsUtils.handle(ex);
		}
	}

	private static Method getMethod(Object hook, String methodName, Class<?> clazz) {
		for (Method method : hook.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				if (method.getParameterTypes().length != 1) {
					continue;
				}
				if (method.getParameterTypes()[0].isAssignableFrom(clazz)) {
					method.setAccessible(true);
					return method;
				}
			}
		}
		return null;
	}
}
