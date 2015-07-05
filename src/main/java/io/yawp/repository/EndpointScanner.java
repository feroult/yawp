package io.yawp.repository;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.actions.Action;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.transformers.Transformer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class EndpointScanner {

	private boolean enableHooks;

	private Reflections endpointsPackage;

	private Map<Class<?>, EndpointFeatures<?>> endpoints;

	public EndpointScanner(String packagePrefix) {
		endpointsPackage = new Reflections(packagePrefix);
		endpoints = new HashMap<>();
		enableHooks = true;
	}

	private void scanEndpoints() {
		Set<Class<?>> clazzes = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);

		for (Class<?> endpoint : clazzes) {
			EndpointFeatures<?> features = new EndpointFeatures<>(endpoint);
			features.setParent(EntityUtils.getParentClass(endpoint));
			endpoints.put(endpoint, features);
		}
	}

	public RepositoryFeatures scan() {
		return new RepositoryFeatures(generateEndpointsMap());
	}

	private Collection<EndpointFeatures<?>> generateEndpointsMap() {
		scanEndpoints();
		scanActions();
		scanTransformers();
		if (enableHooks) {
			scanHooks();
		}
		scanShields();
		return endpoints.values();
	}

	private void scanShields() {
		Set<Class<? extends Shield>> clazzes = endpointsPackage.getSubTypesOf(Shield.class);

		for (Class<? extends Shield> shieldClazz : clazzes) {
			if (Modifier.isAbstract(shieldClazz.getModifiers())) {
				continue;
			}
			setShield(shieldClazz);
		}
	}

	private <T, V extends Shield<T>> void setShield(Class<V> shieldClazz) {
		Class<T> objectClazz = EntityUtils.getShieldObject(shieldClazz);
		for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, shieldClazz.getSimpleName())) {
			endpoint.setShield(shieldClazz);
		}
	}

	private void scanHooks() {
		Set<Class<? extends Hook>> clazzes = endpointsPackage.getSubTypesOf(Hook.class);

		for (Class<? extends Hook> hookClazz : clazzes) {
			addHook(hookClazz);
		}
	}

	private <T, V extends Hook<T>> void addHook(Class<V> hookClazz) {
		Class<T> objectClazz = EntityUtils.getHookObject(hookClazz);
		for (EndpointFeatures<? extends T> endpoint : getEndpoints(objectClazz, hookClazz.getSimpleName())) {
			endpoint.addHook(hookClazz);
		}
	}

	private void scanTransformers() {
		Set<Class<? extends Transformer>> clazzes = endpointsPackage.getSubTypesOf(Transformer.class);

		for (Class<? extends Transformer> transformerClazz : clazzes) {
			Class<?> objectClazz = ReflectionUtils.getGenericParameter(transformerClazz);
			addTransformerForObject(objectClazz, transformerClazz);
		}
	}

	private void addTransformerForObject(Class<?> objectClazz, Class<? extends Transformer> transformerClazz) {
		for (Method method : transformerClazz.getDeclaredMethods()) {
			if (!isValidTransformerMethod(method)) {
				continue;
			}

			for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, transformerClazz.getSimpleName())) {
				endpoint.addTransformer(method.getName(), method);
			}
		}
	}

	private boolean isValidTransformerMethod(Method method) {
		return !method.isSynthetic();
	}

	private void scanActions() {
		Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

		for (Class<? extends Action> actionClazz : clazzes) {
			Class<?> objectClazz = ReflectionUtils.getGenericParameter(actionClazz);

			if (objectClazz == null) {
				continue;
			}

			for (Method method : actionClazz.getDeclaredMethods()) {
				addAction(objectClazz, method);
			}
		}
	}

	// TODO refactor use Java 8 receive io.yawp consumer
	// TODO should we think that an objectClazz has more than one endpoint?
	private <T> List<EndpointFeatures<? extends T>> getEndpoints(Class<T> objectClazz, String featureClazz) {
		List<EndpointFeatures<? extends T>> list = new ArrayList<>();
		for (Class<?> endpoint : endpoints.keySet()) {
			if (objectClazz.isAssignableFrom(endpoint)) {
				list.add((EndpointFeatures<T>) endpoints.get(endpoint));
			}
		}
		if (list.isEmpty()) {
			throw new RuntimeException("Tryed to create feature '" + featureClazz + "' with entity '" + objectClazz.getSimpleName()
					+ "' that is not an @Endpoint nor do any io.yawp inherits from it.");
		}
		return list;
	}

	private void addAction(Class<?> objectClazz, Method method) {
		List<ActionKey> actionKeys = new ArrayList<>(2);

		for (HttpVerb verb : HttpVerb.values()) {
			if (verb.hasAnnotation(method)) {
				String value = verb.getAnnotationValue(method);
				actionKeys.add(new ActionKey(verb, value, overCollection(objectClazz, method)));
			}
		}

		if (actionKeys.isEmpty()) {
			return;
		}

		boolean overCollection = actionKeys.get(0).isOverCollection();
		for (int i = 1; i < actionKeys.size(); i++) {
			validate(actionKeys.get(i).isOverCollection() == overCollection, "If your action " + method.getName() + " for yawp "
					+ objectClazz.getSimpleName() + " has more than one annotation, they must all share the same overCollection value.");
		}

		assertValidActionMethod(objectClazz, method, overCollection);

		for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, method.getDeclaringClass().getSimpleName())) {
			for (ActionKey ar : actionKeys) {
				endpoint.addAction(ar, method);
			}
		}
	}

	private boolean overCollection(Class<?> objectClazz, Method method) {
		Type[] parameters = method.getGenericParameterTypes();

		if (parameters.length == 0) {
			return true;
		}

		if (parameters[0].equals(Map.class)) {
			return true;
		}

		ParameterizedType pType = (ParameterizedType) parameters[0];
		return !pType.getActualTypeArguments()[0].equals(objectClazz);
	}

	private void validate(boolean b, String message) {
		if (!b) {
			throw new RuntimeException(message);
		}
	}

	private void assertValidActionMethod(Class<?> objectClazz, Method method, boolean overCollection) {
		String partialActionMessage = "Invalid action " + method.getName() + " for io.yawp " + objectClazz.getSimpleName()
				+ ". The annotated action methods must have one of three possible signatures: ";
		String invalidActionMessage;
		if (overCollection) {
			invalidActionMessage = partialActionMessage
					+ "It can have no args, when it has no @Parent; it can have one arg only, an IdRef<?> refering the parentId, that will be null if there is none, or it can receive both that id and also a Map<String, String> of params. ";
		} else {
			invalidActionMessage = partialActionMessage
					+ "It can have one arg only, an IdRef<T> when applied over a single object T, or it can receive both that id and also a Map<String, String> of params. ";
		}

		Class<?>[] parameterTypes = method.getParameterTypes();
		validate(parameterTypes.length <= 2, invalidActionMessage);
		if (parameterTypes.length == 2) {
			validate(Map.class.equals(parameterTypes[1]), invalidActionMessage);
			// TODO fix and re-enable validation!
			// if (false) {
			// Type[] types =
			// ReflectionUtils.getGenericParameters(parameterTypes[1]);
			// validate(types.length == 2, invalidActionMessage);
			// for (Type type : types) {
			// validate(type.equals(String.class), invalidActionMessage);
			// }
			// }
		}
		if (parameterTypes.length >= 1) {
			validate(IdRef.class.equals(parameterTypes[0]) || Map.class.equals(parameterTypes[0]), invalidActionMessage);
			// TODO fix and re-enable validation!
			// if (!overCollection && false) {
			// Type[] types =
			// ReflectionUtils.getGenericParameters(parameterTypes[0]);
			// validate(types.length == 1, invalidActionMessage);
			// validate(types[0].equals(objectClazz), invalidActionMessage);
			// }
		} else {
			validate(overCollection, invalidActionMessage);
		}
	}

	public EndpointScanner enableHooks(boolean enableHooks) {
		this.enableHooks = enableHooks;
		return this;
	}

}
