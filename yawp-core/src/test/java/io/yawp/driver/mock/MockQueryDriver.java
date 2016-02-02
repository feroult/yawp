package io.yawp.driver.mock;

import io.yawp.driver.api.QueryDriver;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MockQueryDriver implements QueryDriver {

    @Override
    public <T> List<T> objects(QueryBuilder<?> builder) {
        return generateResults(builder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) {
        List<IdRef<T>> ids = new ArrayList<IdRef<T>>();

        List<Object> objects = generateResults(builder);
        for (Object object : objects) {
            ObjectHolder objectHolder = new ObjectHolder(object);
            ids.add((IdRef<T>) objectHolder.getId());
        }

        return ids;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T fetch(IdRef<T> id) {
        return (T) MockStore.get(id);
    }

    private <T> List<T> generateResults(QueryBuilder<?> builder) {
        List<Object> objects = queryWhere(builder);

        sortList(builder, objects);

        List<T> resultFromCursor = applyCursor(builder, objects);
        List<T> result = applyLimit(builder, resultFromCursor);

        updateCursor(builder, result);

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> applyCursor(QueryBuilder<?> builder, List<Object> objects) {
        if (builder.getCursor() == null) {
            return (List<T>) objects;
        }

        List<T> result = new ArrayList<T>();

        IdRef<?> cursorId = MockStore.getCursor(builder.getCursor());
        boolean startAdd = false;

        for (Object object : objects) {
            if (startAdd) {
                result.add((T) object);
                continue;
            }
            ObjectHolder objectHolder = new ObjectHolder(object);
            if (cursorId.equals(objectHolder.getId())) {
                startAdd = true;
            }
        }

        return result;
    }

    private <T> void updateCursor(QueryBuilder<?> builder, List<T> result) {
        if (result.size() == 0) {
            return;
        }
        T cursorObject = result.get(result.size() - 1);

        if (builder.getCursor() == null) {
            builder.setCursor(MockStore.createCursor(cursorObject));
        } else {
            MockStore.updateCursor(builder.getCursor(), cursorObject);
        }
    }

    private <T> List<T> applyLimit(QueryBuilder<?> builder, List<T> objects) {
        if (builder.getLimit() != null) {
            int limit = builder.getLimit() > objects.size() ? objects.size() : builder.getLimit();
            return (List<T>) objects.subList(0, limit);
        }

        return (List<T>) objects;
    }

    private List<Object> queryWhere(QueryBuilder<?> builder) {
        List<Object> objectsInStore = MockStore.list(builder.getModel().getClazz(), builder.getParentId());

        BaseCondition condition = builder.getCondition();
        List<Object> result = new ArrayList<Object>();

        for (Object object : objectsInStore) {
            if (condition != null && !condition.evaluate(object)) {
                continue;
            }

            result.add(object);
        }
        return result;
    }

    public void sortList(QueryBuilder<?> builder, List<?> objects) {
        final List<QueryOrder> preOrders = builder.getPreOrders();

        if (preOrders.size() == 0) {
            return;
        }

        Collections.sort(objects, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                for (QueryOrder order : preOrders) {
                    int compare = order.compare(o1, o2);

                    if (compare == 0) {
                        continue;
                    }

                    return compare;
                }
                return 0;
            }
        });
    }
}
