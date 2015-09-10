package io.yawp.repository.driver.appengine;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.concurrent.Future;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.utils.FutureWrapper;

public class FutureIdRef extends FutureWrapper<Key, IdRef<?>> {

	private Repository r;

	public FutureIdRef(Repository r, Future<Key> futureKey) {
		super(futureKey);
		this.r = r;
	}

	@Override
	protected Throwable convertException(Throwable t) {
		return t;
	}

	@Override
	protected IdRef<?> wrap(Key key) throws Exception {
		return IdRefToKey.toIdRef(r, key);
	}

}
