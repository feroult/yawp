package io.yawp.repository;

import io.yawp.repository.hooks.RepositoryHooks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureObject<T> {

	private Repository r;

	private Future<IdRef<?>> futureIdRef;

	private T object;

	private boolean enableHooks;

	public FutureObject(Repository r, Future<IdRef<?>> futureIdRef, T object) {
		this.r = r;
		this.futureIdRef = futureIdRef;
		this.object = object;
	}

	public void setEnableHooks(boolean enableHooks) {
		this.enableHooks = enableHooks;
	}

	public T get() {
		try {
			setObjectId();

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

}
