package endpoint.repository;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import endpoint.repository.actions.Action;
import endpoint.repository.actions.ActionRef;
import endpoint.repository.actions.HttpVerb;
import endpoint.repository.actions.annotations.GET;
import endpoint.repository.actions.annotations.PUT;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.hooks.Hook;
import endpoint.repository.transformers.Transformer;
import endpoint.utils.EntityUtils;
import endpoint.utils.ReflectionUtils;

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
			endpoints.put(endpoint, new EndpointFeatures<>(endpoint));
		}
	}

	public RepositoryFeaturesCache scan() {
		return new RepositoryFeaturesCache(generateEndpointsMap());
	}

	private Collection<EndpointFeatures<?>> generateEndpointsMap() {
		scanEndpoints();
		scanActions();
		scanTransformers();
		if (enableHooks) {
			scanHooks();
		}
		return endpoints.values();
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
			for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, transformerClazz.getSimpleName())) {
				endpoint.addTransformer(method.getName(), method);
			}
		}
	}

	private void scanActions() {
		Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

		for (Class<? extends Action> actionClazz : clazzes) {
			Class<?> objectClazz = ReflectionUtils.getGenericParameter(actionClazz);

			for (Method method : actionClazz.getDeclaredMethods()) {
				addAction(objectClazz, method);
			}
		}
	}

	// TODO refactor use Java 8 receive endpoint consumer
	private <T> List<EndpointFeatures<? extends T>> getEndpoints(Class<T> objectClazz, String featureClazz) {
		List<EndpointFeatures<? extends T>> list = new ArrayList<>();
		for (Class<?> endpoint : endpoints.keySet()) {
			if (objectClazz.isAssignableFrom(endpoint)) {
				list.add((EndpointFeatures<T>) endpoints.get(endpoint));
			}
		}
		if (list.isEmpty()) {
			throw new RuntimeException("Tryed to create feature '" + featureClazz + "' with entity '" + objectClazz.getSimpleName()
					+ "' that is not an @Endpoint nor do any endpoint inherits from it.");
		}
		return list;
	}

	private void addAction(Class<?> objectClazz, Method method) {
		List<ActionRef> ars = new ArrayList<>(2);
		GET get = method.getAnnotation(GET.class);
		if (get != null) {
			ars.add(new ActionRef(HttpVerb.GET, get.value(), get.overCollection()));
		}
		PUT put = method.getAnnotation(PUT.class);
		if (put != null) {
			ars.add(new ActionRef(HttpVerb.PUT, put.value(), put.overCollection()));
		}

		if (ars.isEmpty()) {
			return;
		}

		boolean overCollection = ars.get(0).isOverCollection();
		for (int i = 1; i < ars.size(); i++) {
			validate(ars.get(i).isOverCollection() == overCollection,
					"If your action " + method.getName() + " for endpoint " + objectClazz.getSimpleName()
							+ " has more than one annotation, they must all share the same overCollection value.");
		}

		assertValidActionMethod(objectClazz, method, overCollection);

		for (EndpointFeatures<?> endpoint : getEndpoints(objectClazz, method.getDeclaringClass().getSimpleName())) {
			for (ActionRef ar : ars) {
				endpoint.addAction(ar, method);
			}
		}
	}

	private void validate(boolean b, String message) {
		if (!b) {
			throw new RuntimeException(message);
		}
	}

	private void assertValidActionMethod(Class<?> objectClazz, Method method, boolean overCollection) {
		String partialActionMessage = "Invalid action " + method.getName() + " for endpoint " + objectClazz.getSimpleName()
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
			if (false) {
				Type[] types = ReflectionUtils.getGenericParameters(parameterTypes[1]);
				validate(types.length == 2, invalidActionMessage);
				for (Type type : types) {
					validate(type.equals(String.class), invalidActionMessage);
				}
			}
		}
		if (parameterTypes.length >= 1) {
			validate(IdRef.class.equals(parameterTypes[0]), invalidActionMessage);
			// TODO fix and re-enable validation!
			if (!overCollection && false) {
				Type[] types = ReflectionUtils.getGenericParameters(parameterTypes[0]);
				validate(types.length == 1, invalidActionMessage);
				validate(types[0].equals(objectClazz), invalidActionMessage);
			}
		} else {
			validate(overCollection, invalidActionMessage);
		}
	}

	public EndpointScanner enableHooks(boolean enableHooks) {
		this.enableHooks = enableHooks;
		return this;
	}
}
