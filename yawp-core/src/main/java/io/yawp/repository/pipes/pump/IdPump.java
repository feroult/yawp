package io.yawp.repository.pipes.pump;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class IdPump<T> extends Pump<IdRef<T>> {

    private transient List<QueryBuilder<T>> queries = new ArrayList<>();

    public IdPump(Class<T> clazz, int batchSize) {
        super(IdRef.class, batchSize);
    }

    @Override
    protected List<IdRef<T>> executeQueryAt(int queryIndex) {
        return queries.get(queryIndex).ids();
    }

    @Override
    protected QueryBuilder<?> getQueryAt(int queryIndex) {
        return queries.get(queryIndex);
    }

    @Override
    public void addQuery(QueryBuilder<?> query) {
        queries.add((QueryBuilder<T>) query);
    }

    @Override
    protected int getQueriesSize() {
        return queries.size();
    }

    // Serialization

    private void writeObject(ObjectOutputStream out) throws IOException {
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        queries = new ArrayList<>();
    }
}
