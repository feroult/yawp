package io.yawp.repository.pipes;

import io.yawp.driver.api.DriverNotImplementedException;
import io.yawp.repository.Repository;

public class RepositoryPipes {

    public static void flux(Repository r, Object object) {
        try {
            Class<?> endpointClazz = object.getClass();

            for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
                Pipe pipe = createPipeInstance(r, pipeClazz);
                pipe.configure(object);
                r.driver().pipes().flux(pipe, object);
            }
        } catch (DriverNotImplementedException e) {
            // TODO: pipes - remove this
        }
    }

    public static void reflux(Repository r, Object object) {
        try {
            Class<?> endpointClazz = object.getClass();

            for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
                Pipe pipe = createPipeInstance(r, pipeClazz);
                pipe.configure(object);
                r.driver().pipes().reflux(pipe, object);
            }
        } catch (DriverNotImplementedException e) {
            // TODO: pipes - remove this
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
