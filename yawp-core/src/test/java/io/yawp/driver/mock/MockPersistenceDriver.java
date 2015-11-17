package io.yawp.driver.mock;

import io.yawp.driver.api.PersistenceDriver;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;
import io.yawp.repository.Repository;

import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

public class MockPersistenceDriver implements PersistenceDriver {

    private Repository r;

    public MockPersistenceDriver(Repository r) {
        this.r = r;
    }

    @Override
    public void save(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);

        setIdIfNecessary(objectHolder);

        MockStore.put(objectHolder.getId(), object, tx());
    }

    private String tx() {
        MockTransactionDriver transationDriver = (MockTransactionDriver) r.currentTransaction();
        if (transationDriver == null) {
            return null;
        }
        return transationDriver.getTx();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> FutureObject<T> saveAsync(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);

        setIdIfNecessary(objectHolder);

        MockStore.put(objectHolder.getId(), object, tx());

        Future<?> futureId = ConcurrentUtils.constantFuture(objectHolder.getId());
        return new FutureObject<T>(r, (Future<IdRef<?>>) futureId, (T) object);
    }

    @Override
    public void destroy(IdRef<?> id) {
        MockStore.remove(id, tx());
    }

    private void setIdIfNecessary(ObjectHolder objectHolder) {
        IdRef<?> id = objectHolder.getId();

        if (id == null) {
            id = createId(objectHolder);
            objectHolder.setId(id);
        }
    }

    private IdRef<?> createId(ObjectHolder objectHolder) {
        IdRef<?> parentId = objectHolder.getParentId();
        if (parentId != null) {
            return parentId.createChildId(objectHolder.getModel().getClazz(), MockStore.nextId());
        }
        return IdRef.create(r, objectHolder.getModel().getClazz(), MockStore.nextId());
    }

}
