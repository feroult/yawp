package endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

import endpoint.actions.Action;
import endpoint.actions.ActionMethod;
import endpoint.annotations.Endpoint;
import endpoint.hooks.Hook;
import endpoint.transformers.Transformer;
import endpoint.utils.EntityUtils;
import endpoint.utils.ReflectionUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class EndpointLoader {

	private boolean enableHooks;
	private Reflections endpointsPackage;
	private HashMap<String, EndpointRef<?>> endpoints;
	private HashMap<Class<?>, String> endpointsClassRefs;
	
	public EndpointLoader(String packagePrefix) {
		endpointsPackage = new Reflections(packagePrefix); 
		endpoints = new HashMap<>();
		endpointsClassRefs = new HashMap<>();
		enableHooks = true;
	}
	
	private HashMap<String, EndpointRef<?>> scanEndpoints() {
		Set<Class<?>> clazzes = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);

		for (Class<?> endpoint : clazzes) {
			Endpoint annotation = endpoint.getAnnotation(Endpoint.class);
			String path = annotation.path();
			endpoints.put(path, new EndpointRef<>(endpoint));
			endpointsClassRefs.put(endpoint, path);
		}
		return endpoints;
	}

	public RepositoryFeatures scan() {
		return new RepositoryFeatures(generateEndpointsMap());
	}

	private HashMap<String, EndpointRef<?>> generateEndpointsMap() {
		scanEndpoints();
		scanActionsAndSpecialIds();
		scanTransformers();
		if (enableHooks) {
			scanHooks();
		}
		return endpoints;
	}

	private void scanHooks() {
		Set<Class<? extends Hook>> clazzes = endpointsPackage.getSubTypesOf(Hook.class);

		for (Class<? extends Hook> hookClazz : clazzes) {
			addHook((Class<? extends Hook<?>>) hookClazz);
		}
	}
	
	private <T, V extends Hook<T>> void addHook(Class<V> hookClazz) {
		Class<T> objectClazz = EntityUtils.getHookObject(hookClazz);
		getEndpoint(objectClazz).addHook(hookClazz);
	}

	private void scanTransformers() {
		Set<Class<? extends Transformer>> clazzes = endpointsPackage.getSubTypesOf(Transformer.class);

		for (Class<? extends Transformer> transformerClazz : clazzes) {
			Class<?> objectClazz = ReflectionUtils.getGenericParameter(transformerClazz);
			addTransformerForObject(objectClazz, transformerClazz);
		}
	}

	private void addTransformerForObject(Class<?> objectClazz, Class<? extends Transformer> transformerClazz) {
		for (Method method : transformerClazz.getMethods()) {
			getEndpoint(objectClazz).addTransformer(method.getName(), method);
		}
	}

	private void scanActionsAndSpecialIds() {
		Set<Class<? extends Action>> clazzes = endpointsPackage.getSubTypesOf(Action.class);

		for (Class<? extends Action> actionClazz : clazzes) {
			Class<?> objectClazz = ReflectionUtils.getGenericParameter(actionClazz);

			for (Method method : actionClazz.getDeclaredMethods()) {
				addAction(objectClazz, method);
			}
		}
	}

	private <T> EndpointRef<T> getEndpoint(Class<T> objectClazz) {
		return (EndpointRef<T>) endpoints.get(endpointsClassRefs.get(objectClazz));
	}

	private void addAction(Class<?> objectClazz, Method method) {
		ActionMethod am = method.getAnnotation(ActionMethod.class);
		if (am != null) {
			getEndpoint(objectClazz).addAction(am.value(), method);
		}
	}

	public EndpointLoader enableHooks(boolean enableHooks) {
		this.enableHooks = enableHooks;
		return this;
	}
}
