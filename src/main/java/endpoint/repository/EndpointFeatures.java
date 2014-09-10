package endpoint.repository;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import endpoint.repository.actions.ActionKey;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.hooks.Hook;

public class EndpointFeatures<T> {

	private Class<T> clazz;

	private Class<?> parentClazz;

	private Map<ActionKey, Method> actions;

	private Map<String, Method> transformers;

	private List<Class<? extends Hook<? super T>>> hooks;

	public EndpointFeatures(Class<T> clazz) {
		this.clazz = clazz;
		this.actions = new HashMap<>();
		this.transformers = new HashMap<>();
		this.hooks = new ArrayList<>();
	}

	public void setParent(Class<?> parentClazz) {
		this.parentClazz = parentClazz;
	}

	public Class<T> getClazz() {
		return this.clazz;
	}

	public Class<?> getParentClass() {
		return this.parentClazz;
	}

	private <V> void assertInexistence(V key, Method method, Map<V, Method> map, String type) {
		if (map.get(key) != null) {
			throw new RuntimeException("Trying to add two " + type + " with the same name '" + key + "' to endpoint "
			        + clazz.getSimpleName() + ": one at " + map.get(key).getDeclaringClass().getSimpleName() + " and the other at "
			        + method.getDeclaringClass().getSimpleName());
		}
	}

	public void addAction(ActionKey actionRef, Method method) {
		assertInexistence(actionRef, method, actions, "Actions");
		actions.put(actionRef, method);
	}

	public void addTransformer(String name, Method method) {
		assertInexistence(name, method, transformers, "Transformers");
		transformers.put(name, method);
	}

	public void addHook(Class<? extends Hook<? super T>> hook) {
		hooks.add(hook);
	}

	public List<Class<? extends Hook<? super T>>> getHooks() {
		return hooks;
	}

	public Method getAction(ActionKey ref) {
		return actions.get(ref);
	}

	public Method getTransformer(String name) {
		return transformers.get(name);
	}

	public Endpoint getEndpointAnnotation() {
		return clazz.getAnnotation(Endpoint.class);
	}

	public String getEndpointPath() {
		Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
		if (endpoint == null) {
			throw new RuntimeException("The class " + clazz + " was used as an entity but was not annotated with @Endpoint.");
		}
		return endpoint.path();
	}

	public boolean hasCustomAction(ActionKey actionKey) {
		return actions.containsKey(actionKey);
	}
}
