package endpoint.models.actions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import endpoint.models.DatastoreException;
import endpoint.models.DatastoreObject;
import endpoint.models.Repository;
import endpoint.models.Target;
import endpoint.servlet.HttpResponse;

public class RepositoryActions {

	private static Map<String, Method> actions = new HashMap<String, Method>();

	private static Map<String, Class<? extends Annotation>> httpAnnotations = new HashMap<String, Class<? extends Annotation>>();

	private static Set<String> packages = new HashSet<String>();

	public static void initHttpAnnotations() {
		httpAnnotations.put("GET", GET.class);
		httpAnnotations.put("PUT", PUT.class);
	}

	public static void scan(String packagePrefix) {
		if (httpAnnotations.isEmpty()) {
			initHttpAnnotations();
		}

		if (packages.contains(packagePrefix)) {
			return;
		}

		Reflections reflections = new Reflections(packagePrefix);
		Set<Class<? extends Action>> clazzes = reflections.getSubTypesOf(Action.class);

		for (Class<? extends Action> actionClazz : clazzes) {
			Target annotation = actionClazz.getAnnotation(Target.class);
			Class<? extends DatastoreObject> objectClazz = annotation.value();

			addActionForObject(objectClazz, actionClazz);
		}

		packages.add(packagePrefix);
	}

	private static void addActionForObject(Class<? extends DatastoreObject> objectClazz, Class<? extends Action> actionClazz) {
		for (String httpMethod : httpAnnotations.keySet()) {
			Class<? extends Annotation> httpMethodAnnotation = httpAnnotations.get(httpMethod);

			for (Method method : actionClazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(httpMethodAnnotation)) {
					String action = getActionValue(httpMethodAnnotation, method);
					String actionKey = getActionKey(objectClazz, httpMethod, action);

					if (actions.containsKey(actionKey)) {
						throw new RuntimeException("Duplicated action for object: " + actionKey);
					}
					actions.put(actionKey, method);
				}
			}
		}
	}

	private static String getActionKey(Class<? extends DatastoreObject> objectClazz, String httpMethod, String action) {
		return String.format("%s-%s-%s", objectClazz.getSimpleName(), httpMethod, action);
	}

	@SuppressWarnings("unchecked")
	public static HttpResponse execute(Repository r, Class<? extends DatastoreObject> objectClazz, String httpMethod, String action, Long id) {

		try {
			Method method = actions.get(getActionKey(objectClazz, httpMethod, action));
			Class<? extends Action> actionClazz = (Class<? extends Action>) method.getDeclaringClass();

			Action actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			Object ret = method.invoke(actionInstance, id);

			if (method.getReturnType().equals(Void.TYPE)) {
				return null;
			}

			return (HttpResponse) ret;
		} catch (Exception e) {
			if (e.getCause() != null && DatastoreException.class.isInstance(e.getCause())) {
				throw (DatastoreException) e.getCause();
			}

			throw new RuntimeException(e);
		}
	}

	private static String getActionValue(Class<? extends Annotation> httpMethodAnnotation, Method method) {
		try {
			Annotation annotation = method.getAnnotation(httpMethodAnnotation);
			Class<? extends Annotation> annotationClazz = annotation.annotationType();
			Method annotationValueMethod = annotationClazz.getMethod("value");
			String action = (String) annotationValueMethod.invoke(annotation);

			return action;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
