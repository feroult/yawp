package endpoint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import endpoint.annotations.Endpoint;
import endpoint.hooks.Hook;

public class EndpointRef<T> {

	private Class<T> clazz;
	private Map<String, Method> actions;
	private Map<String, Method> transformers;
	private List<Class<? extends Hook<T>>> hooks;
	
	public EndpointRef(Class<T> clazz) {
		this.clazz = clazz;
		this.actions = new HashMap<>();
		this.transformers = new HashMap<>();
		this.hooks = new ArrayList<>();
	}
	
	public Class<T> getClazz() {
		return this.clazz;
	}

	private void assertInexistence(String name, Method method, Map<String, Method> map, String type) {
		if (map.get(name) != null) {
			throw new RuntimeException("Trying to add two " + type + " with the same name '" + name + "' to endpoint " + clazz.getSimpleName() + ": one at " + transformers.get(name).getDeclaringClass().getSimpleName() + " and the other at " + method.getDeclaringClass().getSimpleName());
		}
	}

	public void addAction(String name, Method method) {
		assertInexistence(name, method, actions, "Actions");
		actions.put(name, method);
	}

	public void addTransformer(String name, Method method) {
		assertInexistence(name, method, transformers, "Transformers");
		transformers.put(name, method);
	}

	public void addHook(Class<? extends Hook<T>> hook) {
		hooks.add(hook);
	}

	public List<Class<? extends Hook<T>>> getHooks() {
		return hooks;
	}

	public Method getAction(String name) {
		return actions.get(name);
	}

	public Method getTransformer(String name) {
		return transformers.get(name);
	}

	public Endpoint getEndpointAnnotation() {
		return this.clazz.getAnnotation(Endpoint.class);
	}
}
