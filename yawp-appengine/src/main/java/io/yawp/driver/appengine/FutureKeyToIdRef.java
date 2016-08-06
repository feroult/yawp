package io.yawp.driver.appengine;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.utils.FutureWrapper;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectModel;

import java.util.concurrent.Future;

public class FutureKeyToIdRef extends FutureWrapper<Key, IdRef<?>> {

    private Repository r;
    private ObjectModel model;

    public FutureKeyToIdRef(Repository r, Future<Key> futureKey, ObjectModel model) {
        super(futureKey);
        this.r = r;
        this.model = model;
    }

    @Override
    protected Throwable convertException(Throwable t) {
        return t;
    }

    @Override
    protected IdRef<?> wrap(Key key) throws Exception {
        return IdRefToKey.toIdRef(r, key, model);
    }

}
