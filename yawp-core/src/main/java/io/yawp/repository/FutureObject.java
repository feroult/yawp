package io.yawp.repository;

import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.repository.models.ObjectHolder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureObject<T> {

    private Repository r;

    private Future<IdRef<?>> futureIdRef;

    private Future<?> futureObject;

    private T object;

    private boolean enableHooks;

    public FutureObject(Repository r, Future<IdRef<?>> futureIdRef, T object) {
        this.r = r;
        this.futureIdRef = futureIdRef;
        this.object = object;
    }

    public FutureObject(Repository r, Future<?> futureObject) {
        this.r = r;
        this.futureObject = futureObject;
    }

    public void setEnableHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
    }

    public T get() {
        try {
            if (futureIdRef != null) {
                setObjectId();
            } else {
                setObject();
            }

            if (enableHooks) {
                RepositoryHooks.afterSave(r, object);
            }

            return object;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void setObjectId() throws InterruptedException, ExecutionException {
        ObjectHolder objectHolder = new ObjectHolder(object);
        objectHolder.setId(futureIdRef.get());
    }

    private void setObject() throws ExecutionException, InterruptedException {
        this.object = (T) futureObject.get();
    }

}
