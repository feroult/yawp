package io.yawp.repository.pipes;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.NoResultException;

public class RepositoryPipes {

    public static void flux(Repository r, Object object) {
        Class<?> endpointClazz = object.getClass();

        if (!hasPipes(r, endpointClazz)) {
            return;
        }

        for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
            Pipe pipe = createPipeInstance(r, pipeClazz);
            pipe.configure(object);
            r.driver().pipes().flux(pipe, object);
        }
    }

    public static void reflux(Repository r, IdRef<?> id) {
        Class<?> endpointClazz = id.getClazz();

        if (!hasPipes(r, endpointClazz)) {
            return;
        }

        // Load existing object only one time? Sshield may load it too.
        Object object;
        try {
            object = id.fetch();
        } catch (NoResultException e) {
            return;
        }

        for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
            Pipe pipe = createPipeInstance(r, pipeClazz);
            pipe.configure(object);
            r.driver().pipes().reflux(pipe, object);
        }
    }

    public static void refluxOld(Repository r, Object object) {
        Class<?> endpointClazz = object.getClass();

        if (!hasPipes(r, endpointClazz)) {
            return;
        }

        Object oldObject;

        try {
            oldObject = fetchOldObject(object);
        } catch (NoResultException e) {
            return;
        }

        for (Class<? extends Pipe> pipeClazz : r.getEndpointFeatures(endpointClazz).getPipes()) {
            Pipe oldPipe = createOldPipeInstance(r, pipeClazz, object, oldObject);

            if (!oldPipe.hasSinks()) {
                continue;
            }

            r.driver().pipes().reflux(oldPipe, object);
        }
    }

    private static Pipe createOldPipeInstance(Repository r, Class<? extends Pipe> pipeClazz, Object object, Object oldObject) {
        Pipe oldPipe = createPipeInstance(r, pipeClazz);
        oldPipe.configure(oldObject);

        Pipe newPipe = createPipeInstance(r, pipeClazz);
        newPipe.configure(object);

        oldPipe.removeSinks(newPipe.getSinks());
        return oldPipe;
    }

    private static Object fetchOldObject(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);

        if (objectHolder.getId() == null) {
            throw new NoResultException();
        }

        return objectHolder.getId().fetch();
    }

    public static boolean hasPipes(Repository r, Class<?> endpointClazz) {
        return r.getFeatures() != null && r.getEndpointFeatures(endpointClazz).getPipes().size() != 0;
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
