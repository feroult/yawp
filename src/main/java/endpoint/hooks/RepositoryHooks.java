package endpoint.hooks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import endpoint.DatastoreException;
import endpoint.DatastoreObject;
import endpoint.Repository;
import endpoint.Target;

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
			Class<? extends DatastoreObject> objectClazz = null;

			Target annotation = hookClazz.getAnnotation(Target.class);
			if (annotation != null) {
				objectClazz = annotation.value();
			} else {
				objectClazz = DatastoreObject.class;
			}

			addHookForObject(objectClazz, hookClazz);
		}

		packages.add(packagePrefix);
	}

	private static void addHookForObject(Class<? extends DatastoreObject> objectClazz, Class<? extends Hook> clazz) {
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

	public static void beforeSave(Repository r, DatastoreObject object) {
		invokeHooks(r, object, "beforeSave");
	}

	public static void afterSave(Repository r, DatastoreObject object) {
		invokeHooks(r, object, "afterSave");
	}

	private static void invokeHooks(Repository r, DatastoreObject object, String methodName) {
		List<Class<? extends Hook>> objectHooks = new ArrayList<Class<? extends Hook>>();
		if (hooks.containsKey(object.getClass().getSimpleName())) {
			objectHooks.addAll(hooks.get(object.getClass().getSimpleName()));
		}

		if (hooks.containsKey(DatastoreObject.class.getSimpleName())) {
			objectHooks.addAll(hooks.get(DatastoreObject.class.getSimpleName()));
		}

		for (Class<? extends Hook> hookClazz : objectHooks) {
			invokeHookMethod(r, object, methodName, hookClazz);
		}
	}

	private static void invokeHookMethod(Repository r, DatastoreObject object, String methodName, Class<? extends Hook> hookClazz) {
		try {
			Hook hook = hookClazz.newInstance();
			hook.setRepository(r);

			Method method = null;

			method = getMethod(hook, methodName, DatastoreObject.class);

			if (method == null) {
				method = getMethod(hook, methodName, object.getClass());
			}

			if (method == null) {
				return;
			}

			method.invoke(hook, object);
		} catch (Exception e) {
			if (e.getCause() != null && DatastoreException.class.isInstance(e.getCause())) {
				throw (DatastoreException) e.getCause();
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
