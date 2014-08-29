package endpoint.repository;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import endpoint.repository.actions.ActionRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.hooks.Hook;

public class EndpointFeatures<T> {

	private Class<T> clazz;

	private Map<ActionRef, Method> actions;

	private Map<String, Method> transformers;

	private List<Class<? extends Hook<? super T>>> hooks;

	public EndpointFeatures(Class<T> clazz) {
		this.clazz = clazz;
		this.actions = new HashMap<>();
		this.transformers = new HashMap<>();
		this.hooks = new ArrayList<>();
	}

	public Class<T> getClazz() {
		return this.clazz;
	}

	private <V> void assertInexistence(V key, Method method, Map<V, Method> map, String type) {
		if (map.get(key) != null) {
			throw new RuntimeException("Trying to add two " + type + " with the same name '" + key + "' to endpoint "
					+ clazz.getSimpleName() + ": one at " + map.get(key).getDeclaringClass().getSimpleName() + " and the other at "
					+ method.getDeclaringClass().getSimpleName());
		}
	}

	public void addAction(ActionRef actionRef, Method method) {
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

	public Method getAction(ActionRef ref) {
		return actions.get(ref);
	}

	public Method getTransformer(String name) {
		return transformers.get(name);
	}

	public Endpoint getEndpointAnnotation() {
		return this.clazz.getAnnotation(Endpoint.class);
	}
}
