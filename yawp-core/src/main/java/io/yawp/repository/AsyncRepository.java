package io.yawp.repository;

public class AsyncRepository {

    Repository r;

    public AsyncRepository(Repository r) {
        this.r = r;
    }

    public <T> FutureObject<T> save(T object) {
        return r.saveAsync(object);
    }

    public <T> FutureObject<T> saveWithHooks(T object) {
        return r.saveAsyncWithHooks(object);
    }

}
