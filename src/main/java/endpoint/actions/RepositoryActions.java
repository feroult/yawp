package endpoint.actions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import endpoint.HttpException;
import endpoint.Repository;
import endpoint.Target;
import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;
import endpoint.utils.ThrownExceptionsUtils;

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
			if (!actionClazz.isAnnotationPresent(Target.class)) {
				continue;
			}
			Target annotation = actionClazz.getAnnotation(Target.class);
			Class<?> objectClazz = annotation.value();

			addActionForObject(objectClazz, actionClazz);
		}

		packages.add(packagePrefix);
	}

	private static void addActionForObject(Class<?> objectClazz, Class<? extends Action> actionClazz) {
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

	private static String getActionKey(Class<?> objectClazz, String httpMethod, String action) {
		return String.format("%s-%s-%s", objectClazz.getSimpleName(), httpMethod, action);
	}

	public static HttpResponse execute(Repository r, Class<?> objectClazz, String httpMethod, String action, Long id,
			Map<String, String> params) throws HttpException {

		try {
			Method method = actions.get(getActionKey(objectClazz, httpMethod, action));

			if (method == null) {
				throw new HttpException(404);
			}

			@SuppressWarnings("unchecked")
			Class<? extends Action> actionClazz = (Class<? extends Action>) method.getDeclaringClass();

			Action actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			Object ret;
			if (method.getParameterTypes().length == 0) {
				ret = method.invoke(actionInstance);
			} else if (method.getParameterTypes().length == 1) {
				ret = method.invoke(actionInstance, id);
			} else {
				ret = method.invoke(actionInstance, id, params);
			}

			if (method.getReturnType().equals(Void.TYPE)) {
				return null;
			}

			if (HttpResponse.class.isInstance(ret)) {
				return (HttpResponse) ret;
			}

			return new JsonResponse(JsonUtils.to(ret));

		} catch (Exception e) {
			throw ThrownExceptionsUtils.handle(e);
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
