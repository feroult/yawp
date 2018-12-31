package io.yawp.repository;

public interface FutureObjectHook<T> {
    void apply(Repository r, T object);
}
