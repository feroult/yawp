package io.yawp.driver.postgresql;

import io.yawp.driver.api.QueryDriver;
import io.yawp.driver.postgresql.datastore.*;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class PGQueryDriver implements QueryDriver {

    private Repository r;

    private Datastore datastore;

    private EntityToObjectConverter toObject;

    public PGQueryDriver(Repository r, ConnectionManager connectionManager) {
        this.r = r;
        this.datastore = Datastore.create(connectionManager);
        this.toObject = new EntityToObjectConverter(r);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> objects(QueryBuilder<?> builder) {
        try {
            List<Entity> queryResult = generateResults(builder, false);

            List<T> objects = new ArrayList<>();

            for (Entity entity : queryResult) {
                objects.add((T) toObject.convert(builder.getModel(), entity));
            }

            return objects;
        } catch (FalsePredicateException e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) {
        try {
            List<Entity> queryResult = generateResults(builder, false);

            List<IdRef<T>> ids = new ArrayList<>();

            for (Entity entity : queryResult) {
                ids.add((IdRef<T>) IdRefToKey.toIdRef(r, entity.getKey(), builder.getModel()));
            }

            return ids;
        } catch (FalsePredicateException e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T fetch(IdRef<T> id) {
        try {
            Key key = IdRefToKey.toKey(r, id);
            Entity entity = datastore.get(key);
            return (T) toObject.convert(id.getModel(), entity);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public <T> FutureObject<T> fetchAsync(IdRef<T> id) {
        T object = fetch(id);
        Future<T> futureObject = ConcurrentUtils.constantFuture(object);
        return new FutureObject<>(r, futureObject);
    }

    // query

    private List<Entity> generateResults(QueryBuilder<?> builder, boolean keysOnly) throws FalsePredicateException {
        return datastore.query(new Query(builder, keysOnly));
    }

}
