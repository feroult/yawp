package io.yawp.repository;

import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.utils.EntityUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.appengine.api.datastore.Key;

public class FutureObject<T> {

	private Repository r;

	private Future<Key> futureKey;

	private T object;

	private boolean enableHooks;

	public FutureObject(Repository r, Future<Key> futureKey, T object, boolean enableHooks) {
		this.r = r;
		this.futureKey = futureKey;
		this.object = object;
		this.enableHooks = enableHooks;
	}

	public T get() {
		try {

			Key key = futureKey.get();
			EntityUtils.setKey(r, object, key);

			if (enableHooks) {
				RepositoryHooks.afterSave(r, object);
			}

			return object;

		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
