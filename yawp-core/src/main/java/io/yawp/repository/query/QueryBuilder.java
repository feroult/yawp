package io.yawp.repository.query;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.Condition;
import io.yawp.repository.query.condition.SimpleCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QueryBuilder<T> {

    private Class<T> clazz;

    private ObjectModel model;

    private Repository r;

    private IdRef<?> parentId;

    private BaseCondition condition;

    private List<QueryOrder> preOrders = new ArrayList<QueryOrder>();

    private List<QueryOrder> postOrders = new ArrayList<QueryOrder>();

    private Integer limit;

    private String cursor;

    public static <T> QueryBuilder<T> q(Class<T> clazz, Repository r) {
        return new QueryBuilder<T>(clazz, r);
    }

    private QueryBuilder(Class<T> clazz, Repository r) {
        this.clazz = clazz;
        this.r = r;
        this.model = new ObjectModel(clazz);
    }

    public <N> QueryTransformer<T, N> transform(String transformName) {
        return new QueryTransformer<>(this, transformName);
    }

    public QueryBuilder<T> and(String field, String operator, Object value) {
        return where(field, operator, value);
    }

    public QueryBuilder<T> where(String field, String operator, Object value) {
        return where(Condition.c(field, operator, value));
    }

    public QueryBuilder<T> where(BaseCondition c) {
        if (condition == null) {
            condition = c;
        } else {
            condition = Condition.and(condition, c);
        }

        condition.init(r, clazz);
        return this;
    }

    public QueryBuilder<T> and(BaseCondition c) {
        return where(c);
    }

    public QueryBuilder<T> from(IdRef<?> parentId) {
        if (parentId == null) {
            this.parentId = null;
            return this;
        }

        this.parentId = parentId;
        return this;
    }

    public QueryBuilder<T> order(String property) {
        order(property, null);
        return this;
    }

    public QueryBuilder<T> order(String property, String direction) {
        preOrders.add(new QueryOrder(null, property, direction));
        return this;
    }

    public QueryBuilder<T> sort(String property) {
        sort(property, null);
        return this;
    }

    public QueryBuilder<T> sort(String property, String direction) {
        sort(null, property, direction);
        return this;
    }

    public QueryBuilder<T> sort(String entity, String property, String direction) {
        postOrders.add(new QueryOrder(entity, property, direction));
        return this;
    }

    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder<T> cursor(String cursor) {
        this.cursor = cursor;
        return this;
    }

    public IdRef<?> getParentId() {
        return parentId;
    }

    public String getCursor() {
        return this.cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public QueryBuilder<T> options(QueryOptions options) {
        if (options.getCondition() != null) {
            where(options.getCondition());
        }

        if (options.getPreOrders() != null) {
            preOrders.addAll(options.getPreOrders());
        }

        if (options.getPostOrders() != null) {
            postOrders.addAll(options.getPostOrders());
        }

        if (options.getLimit() != null) {
            limit(options.getLimit());
        }

        if (options.getCursor() != null) {
            cursor(options.getCursor());
        }

        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public List<QueryOrder> getPreOrders() {
        return preOrders;
    }

    public BaseCondition getCondition() {
        return condition;
    }

    public Repository getRepository() {
        return this.r;
    }

    public ObjectModel getModel() {
        return model;
    }

    public List<T> executeQueryList() {
        r.namespace().set(getClazz());
        try {
            return executeQuery();
        } finally {
            r.namespace().reset();
        }
    }

    public List<T> list() {
        List<T> list = executeQueryList();
        sortList(list);
        return list;
    }

    public T first() {
        r.namespace().set(getClazz());
        try {
            if (isQueryById()) {
                return executeQueryById();
            }
            return executeQueryFirst();
        } finally {
            r.namespace().reset();
        }
    }

    private T executeQueryFirst() {
        limit(1);

        List<T> list = executeQuery();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public T only() throws NoResultException, MoreThanOneResultException {
        r.namespace().set(getClazz());
        try {
            T object;

            if (isQueryById()) {
                object = executeQueryById();
            } else {
                object = executeQueryOnly();
            }

            if (object == null) {
                throw new NoResultException();
            }

            return object;
        } finally {
            r.namespace().reset();
        }
    }

    private T executeQueryOnly() throws MoreThanOneResultException {
        List<T> list = executeQuery();
        if (list.size() == 0) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new MoreThanOneResultException();
    }

    private List<T> executeQuery() {
        List<T> objects = r.driver().query().objects(this);
        return postFilter(objects);
    }

    private List<T> postFilter(List<T> objects) {
        if (!hasPostFilter()) {
            return objects;
        }
        return condition.applyPostFilter(objects);

    }

    private boolean hasPostFilter() {
        return condition != null && condition.hasPostFilter();
    }

    @SuppressWarnings("unchecked")
    private T executeQueryById() {
        SimpleCondition c = (SimpleCondition) condition;
        IdRef<T> id = (IdRef<T>) c.getWhereValue();

        return r.driver().query().fetch(id);
    }

    private boolean isQueryById() {
        if (condition == null || !(condition instanceof SimpleCondition)) {
            return false;
        }

        SimpleCondition c = (SimpleCondition) condition;
        return c.isIdField() && c.isEqualOperator();
    }

    public void sortList(List<?> objects) {
        if (!hasPostOrder()) {
            return;
        }

        Collections.sort(objects, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                for (QueryOrder order : postOrders) {
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

    public boolean hasPreOrder() {
        return preOrders.size() != 0;
    }

    private boolean hasPostOrder() {
        return postOrders.size() != 0;
    }

    protected Class<T> getClazz() {
        return clazz;
    }

    public QueryBuilder<T> whereById(String operator, IdRef<?> id) {
        return from(id.getParentId()).where(model.getIdField().getName(), operator, id);
    }

    public T fetch(IdRef<?> idRef) {
        return whereById("=", idRef).only();
    }

    public T fetch(Long id) {
        IdRef<?> idRef = IdRef.create(r, clazz, id);
        return fetch(idRef);
    }

    public T fetch(String name) {
        IdRef<?> idRef = IdRef.create(r, clazz, name);
        return fetch(idRef);
    }

    public List<IdRef<T>> ids() {
        if (hasPostFilter() || hasPostOrder()) {
            throw new RuntimeException("ids() cannot be used with post query filter or order. You may need to add @Index to your model attributes.");
        }

        r.namespace().set(getClazz());
        try {
            List<IdRef<T>> ids = r.driver().query().ids(this);


            return ids;
        } finally {
            r.namespace().reset();
        }
    }

    public IdRef<T> onlyId() throws NoResultException, MoreThanOneResultException {

        List<IdRef<T>> ids = ids();

        if (ids.size() == 0) {
            throw new NoResultException();
        }

        if (ids.size() > 1) {
            throw new MoreThanOneResultException();
        }

        return ids.get(0);
    }
}
