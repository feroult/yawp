package endpoint.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import endpoint.Repository;
import endpoint.query.DatastoreQuery;
import endpoint.utils.EntityUtils;
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
		for (Class<? extends Hook<?>> hookClazz : r.getEndpointFeatures(EntityUtils.getEndpointName(targetClazz)).getHooks()) {
			invokeHookMethod(r, object, methodName, hookClazz);
		}
	}

	private static void invokeHookMethod(Repository r, Object object, String methodName, Class<? extends Hook<?>> hookClazz) {
		try {
			Hook<?> hook = hookClazz.newInstance();
			hook.setRepository(r);

			Method hookMethod = getMethod(hook, methodName, object.getClass());
			hookMethod.invoke(hook, object);
		} catch (InstantiationException ex) {
			throw new RuntimeException("The Hook class " + hookClazz.getSimpleName() + " must have a default constructor, and it must not throw exceptions.", ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException("An exception has occured when running the " + methodName + " hook for class " + hookClazz.getSimpleName(), ex);
		} catch (IllegalAccessException | IllegalArgumentException ex) {
			throw ThrownExceptionsUtils.handle(ex);
		}
	}

	private static Method getMethod(Object hook, String methodName, Class<?>... clazz) {
		try {
			Method method = hook.getClass().getDeclaredMethod(methodName, clazz);
			method.setAccessible(true);
			return method;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
}
