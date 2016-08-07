package io.yawp.driver.appengine;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.utils.FutureWrapper;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureEntityToObject<T> extends FutureWrapper<Entity, T> {

    private final Repository r;

    private final Class<?> clazz;

    public FutureEntityToObject(Repository r, Class<?> clazz, Future<Entity> future) {
        super(future);
        this.r = r;
        this.clazz = clazz;
    }

    @Override
    protected T wrap(Entity entity) throws Exception {
        EntityToObjectConverter toObject = new EntityToObjectConverter(r);
        return (T) toObject.convert(new ObjectModel(clazz), entity);
    }

    @Override
    protected Throwable convertException(Throwable t) {
        return t;
    }

    @Override
    public T get() throws ExecutionException, InterruptedException {
        try {
            return super.get();
        } catch (ExecutionException e) {
            return notFoundAsNull(e);
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        try {
            return super.get(timeout, unit);
        } catch (ExecutionException e) {
            return notFoundAsNull(e);
        }
    }

    private T notFoundAsNull(ExecutionException e) throws ExecutionException {
        if (e.getCause() != null && EntityNotFoundException.class.isInstance(e.getCause())) {
            return null;
        }
        throw e;
    }
}
