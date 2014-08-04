package endpoint.hooks;

import endpoint.*;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class RepositoryHooks {
	private static Map<String, List<Class<? extends Hook>>> hooks = new HashMap<String, List<Class<? extends Hook>>>();

	private static Set<String> packages = new HashSet<String>();

	public static void scan(String packagePrefix) {
		if (packages.contains(packagePrefix)) {
			return;
		}

		Reflections reflections = new Reflections(packagePrefix);
		Set<Class<? extends Hook>> clazzes = reflections.getSubTypesOf(Hook.class);

		for (Class<? extends Hook> hookClazz : clazzes) {
			Class<?> objectClazz = null;

			Target annotation = hookClazz.getAnnotation(Target.class);
			if (annotation != null) {
				objectClazz = annotation.value();
			} else {
				objectClazz = Object.class;
			}

			addHookForObject(objectClazz, hookClazz);
		}

		packages.add(packagePrefix);
	}

	private static void addHookForObject(Class<?> objectClazz, Class<? extends Hook> clazz) {
		List<Class<? extends Hook>> objectHooks = null;

		String objectName = objectClazz.getSimpleName();
		if (hooks.containsKey(objectName)) {
			objectHooks = hooks.get(objectName);
		} else {
			objectHooks = new ArrayList<Class<? extends Hook>>();
			hooks.put(objectName, objectHooks);
		}

		objectHooks.add(clazz);
	}

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
		List<Class<? extends Hook>> objectHooks = new ArrayList<Class<? extends Hook>>();
		if (hooks.containsKey(targetClazz.getSimpleName())) {
			objectHooks.addAll(hooks.get(targetClazz.getSimpleName()));
		}

		if (hooks.containsKey(Object.class.getSimpleName())) {
			objectHooks.addAll(hooks.get(Object.class.getSimpleName()));
		}

		for (Class<? extends Hook> hookClazz : objectHooks) {
			invokeHookMethod(r, object, methodName, hookClazz);
		}
	}

	private static void invokeHookMethod(Repository r, Object object, String methodName, Class<? extends Hook> hookClazz) {
		try {
			Hook hook = hookClazz.newInstance();
			hook.setRepository(r);

			Method method = null;

			method = getMethod(hook, methodName, Object.class);

			if (method == null) {
				method = getMethod(hook, methodName, object.getClass());
			}

			if (method == null) {
				return;
			}

			method.invoke(hook, object);
		} catch (Exception e) {
			final Class<? extends RuntimeException>[] allowedExceptions = new Class[]{DatastoreException.class, HttpException.class};

			for (Class klass : allowedExceptions) {
				if (klass.isInstance(e.getCause())) {
					throw (RuntimeException) e.getCause();
				}
			}
			throw new RuntimeException(e);
		}
	}

	private static Method getMethod(Object hook, String methodName, Class<?>... clazz) {
		try {
			return hook.getClass().getMethod(methodName, clazz);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
