package io.yawp.repository.pipes;

import io.yawp.repository.Repository;

public class RepositoryPipes {

    public static void save(Repository r, Object object) {
        Class<?> endpointClazz = object.getClass();

        if (!isEnpointObject(r, endpointClazz)) {
            return;
        }

        for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
            Pipe pipe = createPipeInstance(r, pipeClazz);
            pipe.configure(object);
            r.driver().pipes().save(pipe, object);
        }
    }

    private static Pipe createPipeInstance(Repository r, Class<? extends Pipe> pipeClazz) {
        try {
            Pipe pipe = pipeClazz.newInstance();
            pipe.setRepository(r);

            return pipe;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isEnpointObject(Repository r, Class<?> endpointClazz) {
        return r.getEndpointFeatures(endpointClazz) != null;
    }

}
