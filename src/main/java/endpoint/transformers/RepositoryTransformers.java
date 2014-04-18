package endpoint.transformers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import endpoint.Repository;
import endpoint.Target;

public class RepositoryTransformers {

	private static Map<String, Method> transformers = new HashMap<String, Method>();

	private static Set<String> packages = new HashSet<String>();

	public static void scan(String packagePrefix) {
		if (packages.contains(packagePrefix)) {
			return;
		}

		Reflections reflections = new Reflections(packagePrefix);
		Set<Class<? extends Transformer>> clazzes = reflections.getSubTypesOf(Transformer.class);

		for (Class<? extends Transformer> transformerClazz : clazzes) {
			Target annotation = transformerClazz.getAnnotation(Target.class);
			Class<?> objectClazz = annotation.value();

			addTransformerForObject(objectClazz, transformerClazz);
		}

		packages.add(packagePrefix);
	}

	private static void addTransformerForObject(Class<?> objectClazz, Class<? extends Transformer> transformerClazz) {
		for (Method method : transformerClazz.getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())) {
				String transformerKey = getTransformerKey(objectClazz, method.getName());

				if (transformers.containsKey(transformerKey)) {
					throw new RuntimeException("Duplicated transformer for object: " + transformerKey);
				}
				transformers.put(transformerKey, method);
			}
		}
	}

	private static String getTransformerKey(Class<?> objectClazz, String name) {
		return String.format("%s-%s", objectClazz.getSimpleName(), name);
	}

	@SuppressWarnings("unchecked")
	public static Object execute(Repository r, Object object, String name) {

		try {
			Method method = transformers.get(getTransformerKey(object.getClass(), name));
			Class<? extends Transformer> transformerClazz = (Class<? extends Transformer>) method.getDeclaringClass();

			Transformer transformerInstance = transformerClazz.newInstance();
			transformerInstance.setRepository(r);

			return method.invoke(transformerInstance, object);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}