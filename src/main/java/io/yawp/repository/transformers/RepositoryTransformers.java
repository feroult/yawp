package io.yawp.repository.transformers;

import io.yawp.repository.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RepositoryTransformers {

	@SuppressWarnings("unchecked")
	public static <F, T> T execute(Repository r, F object, String name) {
		try {
			Method method = r.getEndpointFeatures(object.getClass()).getTransformer(name);
			Class<? extends Transformer<F>> transformerClazz = (Class<? extends Transformer<F>>) method.getDeclaringClass();

			Transformer<F> transformerInstance = transformerClazz.newInstance();
			transformerInstance.setRepository(r);

			return (T) method.invoke(transformerInstance, object);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}