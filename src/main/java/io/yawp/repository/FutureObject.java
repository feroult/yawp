package io.yawp.repository;

import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.hooks.RepositoryHooks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.appengine.api.datastore.Key;

public class FutureObject<T> {

	private Repository r;

	private Future<Key> futureKey;

	private T object;

	private ObjectHolder objectH;

	private boolean enableHooks;

	public FutureObject(Repository r, Future<Key> futureKey, T object, boolean enableHooks) {
		this.r = r;
		this.futureKey = futureKey;
		this.object = object;
		this.objectH = new ObjectHolder(object);
		this.enableHooks = enableHooks;
	}

	public T get() {
		try {
			objectH.setId(IdRef.fromKey(r, futureKey.get()));

			if (enableHooks) {
				RepositoryHooks.afterSave(r, object);
			}

			return object;

		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
