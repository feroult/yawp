package io.yawp.repository.pipes;

import io.yawp.repository.Repository;

public class RepositoryPipes {

    public static void save(Repository r, Object object) {
        Class<?> endpointClazz = object.getClass();

        for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
            Pipe pipe = createPipeInstance(r, pipeClazz);
            pipe.init(object);

//            r.driver().tasks().saveToPipe(pipe);
//            r.driver().async().saveToPipe(pipe);
//            r.driver().pipeline().saveToPipe(pipe);
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

}
