package io.yawp.repository;

import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.hooks.RepositoryHooks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureObject<T> {

	private Repository r;

	private Future<IdRef<?>> futureIdRef;

	private ObjectHolder objectH;

	private boolean enableHooks;

	public FutureObject(Repository r, Future<IdRef<?>> futureIdRef, ObjectHolder objectH, boolean enableHooks) {
		this.r = r;
		this.futureIdRef = futureIdRef;
		this.objectH = objectH;
		this.enableHooks = enableHooks;
	}

	@SuppressWarnings("unchecked")
	public T get() {
		try {
			objectH.setId(futureIdRef.get());
			T object = (T) objectH.getObject();

			if (enableHooks) {
				RepositoryHooks.afterSave(r, object);
			}

			return object;

		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
