package io.yawp.repository.actions;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionKey {

	private HttpVerb verb;

	private String actionName;

	private boolean overCollection;

	public ActionKey(HttpVerb verb, String actionName, boolean overCollection) {
		this.verb = verb;
		this.actionName = actionName;
		this.overCollection = overCollection;
	}

	public String getActionName() {
		return actionName;
	}

	public HttpVerb getVerb() {
		return verb;
	}

	public boolean isOverCollection() {
		return this.overCollection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionName == null) ? 0 : actionName.hashCode());
		result = prime * result + (overCollection ? 1231 : 1237);
		result = prime * result + ((verb == null) ? 0 : verb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ActionKey other = (ActionKey) obj;
		if (actionName == null) {
			if (other.actionName != null) {
				return false;
			}
		} else if (!actionName.equals(other.actionName)) {
			return false;
		}
		if (overCollection != other.overCollection) {
			return false;
		}
		if (verb != other.verb) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "<" + this.verb + ">" + this.actionName + (this.overCollection ? "[]" : "");
	}

	public static List<ActionKey> parseMethod(Method method) throws InvalidActionMethodException {
		List<ActionKey> actionKeys = new ArrayList<>();

		for (HttpVerb verb : HttpVerb.values()) {
			if (!verb.hasAnnotation(method)) {
				continue;
			}

			if (!isValidActionMethod(method)) {
				throw new InvalidActionMethodException();
			}

			String value = verb.getAnnotationValue(method);
			actionKeys.add(new ActionKey(verb, value, overCollection(method)));
		}

		return actionKeys;
	}

	private static boolean isValidActionMethod(Method method) {

		if (rootCollection(method)) {
			return true;
		}

		if (singleObject(method)) {
			return true;
		}

		if (parentCollection(method)) {
			return true;
		}

		return false;
	}

	private static boolean parentCollection(Method method) {
		Type[] genericTypes = method.getGenericParameterTypes();
		Type[] types = method.getParameterTypes();

		Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
		Class<?> parentClazz = EntityUtils.getParentClass(objectClazz);

		if (types.length == 1 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(parentClazz)) {
			return true;
		}

		if (types.length == 2 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(parentClazz)
				&& types[1].equals(Map.class)) {
			return true;
		}

		return false;
	}

	private static boolean singleObject(Method method) {
		Type[] genericTypes = method.getGenericParameterTypes();
		Type[] types = method.getParameterTypes();

		Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());

		if (types.length == 1 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(objectClazz)) {
			return true;
		}

		if (types.length == 2 && types[0].equals(IdRef.class) && getParameterType(genericTypes, 0).equals(objectClazz)
				&& types[1].equals(Map.class)) {
			return true;
		}

		return false;
	}

	private static boolean rootCollection(Method method) {
		Type[] types = method.getParameterTypes();

		if (types.length == 0) {
			return true;
		}

		if (types.length == 1 && types[0].equals(Map.class)) {
			return true;
		}

		return false;
	}

	private static Type getParameterType(Type[] parameters, int index) {
		return ((ParameterizedType) parameters[index]).getActualTypeArguments()[index];
	}

	private static boolean overCollection(Method method) {
		Type[] parameters = method.getGenericParameterTypes();

		if (parameters.length == 0) {
			return true;
		}

		if (parameters[0].equals(Map.class)) {
			return true;
		}

		Class<?> objectClazz = ReflectionUtils.getGenericParameter(method.getDeclaringClass());
		ParameterizedType pType = (ParameterizedType) parameters[0];
		return !pType.getActualTypeArguments()[0].equals(objectClazz);
	}

}
