package io.yawp.repository.pipes;

import io.yawp.driver.api.DriverNotImplementedException;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

public class RepositoryPipes {

    public static void save(Repository r, Object object) {
        try {
            Class<?> endpointClazz = object.getClass();

            for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
                Pipe pipe = createPipeInstance(r, pipeClazz);
                pipe.configure(object);
                r.driver().pipes().save(pipe, object);
            }
        } catch (DriverNotImplementedException e) {
            // TODO: pipes - remove this
        }
    }

    public static void destroy(Repository r, IdRef<?> id) {
        try {
            Class<?> endpointClazz = id.getClazz();

            for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
                Pipe pipe = createPipeInstance(r, pipeClazz);
                pipe.configure(id);
                r.driver().pipes().destroy(pipe, id);
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
