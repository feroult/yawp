package io.yawp.repository;

public class AsyncRepository {

    Repository r;

    public AsyncRepository(Repository r) {
        this.r = r;
    }

    public <T> FutureObject<T> save(T object) {
        return r.saveAsync(object);
    }

    public <T> FutureObject<Void> destroy(IdRef<?> id) {
        return r.destroyAsync(id);
    }

    public <T> FutureObject<T> fetch(IdRef<T> id) {
        return r.fetchAsync(id);
    }
}
