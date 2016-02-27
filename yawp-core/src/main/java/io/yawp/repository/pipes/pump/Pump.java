package io.yawp.repository.pipes.pump;

import io.yawp.repository.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class Pump<T> {
    protected Class<T> clazz;

    protected int defaultBatchSize;

    private List<T> objects = new ArrayList<>();

    private int objectsIndex = 0;

    protected int queryIndex = 0;

    private String cursor;

    public Pump(int batchSize) {
        this.defaultBatchSize = batchSize;
    }

    public abstract void addQuery(QueryBuilder<?> query);

    protected abstract QueryBuilder<?> getQueryAt(int queryIndex);

    protected abstract List<T> executeQueryAt(int queryIndex);

    protected abstract int getQueriesSize();


    public void add(T object) {
        objects.add(object);
    }

    public void addAll(List<T> newObjects) {
        objects.addAll(newObjects);
    }

    public List<T> more() {
        if (hasMoreObjects()) {
            List<T> list = moreFromList();
            if (list.size() < defaultBatchSize && hasMoreQueries()) {
                list.addAll(moreFromQuery(defaultBatchSize - list.size()));
            }
            return list;
        }
        return moreFromQuery(defaultBatchSize);
    }

    private List<T> moreFromList() {
        List<T> list = new ArrayList<>();

        int fromIndex = objectsIndex;
        int toIndex = objectsIndex + defaultBatchSize;

        if (toIndex >= objects.size()) {
            toIndex = objects.size();
        }

        objectsIndex = toIndex;

        list.addAll(objects.subList(fromIndex, toIndex));
        return list;
    }

    private List<T> moreFromQuery(int batchSize) {
        int queryIndex = this.queryIndex;
        List<T> list = executeQueryAt(batchSize, queryIndex);
        if (list.size() < batchSize) {
            this.queryIndex++;
            cursor = null;
            if (hasMoreQueries()) {
                list.addAll(moreFromQuery(defaultBatchSize - list.size()));
            }
        }
        return list;
    }

    private List<T> executeQueryAt(int batchSize, int queryIndex) {
        QueryBuilder<?> q = getQueryAt(queryIndex);
        configureQuery(batchSize, q);
        List<T> list = executeQueryAt(queryIndex);
        cursor = q.getCursor();
        return list;
    }

    private void configureQuery(int batchSize, QueryBuilder<?> q) {
        if (cursor != null) {
            q.cursor(cursor);
        }
        q.limit(batchSize);
    }

    public boolean hasMore() {
        return hasMoreObjects() || hasMoreQueries();
    }

    protected boolean hasMoreQueries() {
        return queryIndex < getQueriesSize();
    }

    private boolean hasMoreObjects() {
        return objectsIndex < objects.size();
    }

}
