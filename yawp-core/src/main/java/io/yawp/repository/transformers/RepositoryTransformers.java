package io.yawp.repository.transformers;

import io.yawp.repository.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RepositoryTransformers {

    private RepositoryTransformers() {}

    @SuppressWarnings("unchecked")
    public static <F, T> T execute(Repository r, F object, String name) {
        TransformerRunner transformerRunner = new TransformerRunner<F>(r, object.getClass(), name);
        return (T) transformerRunner.run(object);
    }

    public static <F, T> List<T> execute(Repository r, List<F> list, String name) {
        if (list.size() == 0) {
            return new ArrayList<T>();
        }

        TransformerRunner transformerRunner = new TransformerRunner<F>(r, list.get(0).getClass(), name);

        List<T> transformedList = new ArrayList<T>();

        for (F object : list) {
            transformedList.add((T) transformerRunner.run(object));
        }

        return transformedList;
    }

    private static class TransformerRunner<F> {
        private Repository r;
        private Class<?> endpointClazz;
        private String name;
        private Method method;
        private Transformer<F> transformerInstance;

        public TransformerRunner(Repository r, Class<?> endpointClazz, String name) {
            this.r = r;
            this.endpointClazz = endpointClazz;
            this.name = name;
            build();
        }

        public void build() {
            try {
                method = r.getEndpointFeatures(endpointClazz).getTransformer(name);
                Class<? extends Transformer<F>> transformerClazz = (Class<? extends Transformer<F>>) method.getDeclaringClass();
                transformerInstance = transformerClazz.newInstance();
                transformerInstance.setRepository(r);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public <T, F> T run(F object) {
            try {
                return (T) method.invoke(transformerInstance, object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}