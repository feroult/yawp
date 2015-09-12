package io.yawp.repository;

import io.yawp.repository.hooks.RepositoryHooks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureObject<T> {

	private Repository r;

	private Future<IdRef<?>> futureIdRef;

	private ObjectHolder objectHolder;

	private boolean enableHooks;

	public FutureObject(Repository r, Future<IdRef<?>> futureIdRef, ObjectHolder objectHolder, boolean enableHooks) {
		this.r = r;
		this.futureIdRef = futureIdRef;
		this.objectHolder = objectHolder;
		this.enableHooks = enableHooks;
	}

	@SuppressWarnings("unchecked")
	public T get() {
		try {
			objectHolder.setId(futureIdRef.get());
			T object = (T) objectHolder.getObject();

			if (enableHooks) {
				RepositoryHooks.afterSave(r, object);
			}

			return object;

		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
