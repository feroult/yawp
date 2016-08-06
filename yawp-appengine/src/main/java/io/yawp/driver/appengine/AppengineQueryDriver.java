package io.yawp.driver.appengine;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.driver.api.QueryDriver;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.FieldModel;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Future;

public class AppengineQueryDriver implements QueryDriver {

    // Filter for query
    private static final String NORMALIZED_FIELD_PREFIX = "__";

    private Repository r;

    private EntityToObjectConverter toObject;

    public AppengineQueryDriver(Repository r) {
        this.r = r;
        this.toObject = new EntityToObjectConverter(r);
    }

    private DatastoreService datastore() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    private AsyncDatastoreService asyncDatastore() {
        return DatastoreServiceFactory.getAsyncDatastoreService();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> objects(QueryBuilder<?> builder) {
        try {
            QueryResultList<Entity> queryResult = generateResults(builder, false);

            List<T> objects = new ArrayList<T>();

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
            QueryResultList<Entity> queryResult = generateResults(builder, true);
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
            Entity entity = datastore().get(key);
            return (T) toObject.convert(id.getModel(), entity);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public <T> FutureObject<T> fetchAsync(IdRef<T> id) {
        Key key = IdRefToKey.toKey(r, id);
        Future<Entity> futureEntity = asyncDatastore().get(key);
        return new FutureObject<T>(r, new FutureEntityToObject(r, id.getClazz(), futureEntity));
    }

    // query

    private QueryResultList<Entity> generateResults(QueryBuilder<?> builder, boolean keysOnly) throws FalsePredicateException {
        QueryResultList<Entity> queryResult = prepareQuery(builder, keysOnly).asQueryResultList(configureFetchOptions(builder));
        setCursor(builder, queryResult);
        return queryResult;
    }

    private FetchOptions configureFetchOptions(QueryBuilder<?> builder) {
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (builder.getLimit() != null) {
            fetchOptions.limit(builder.getLimit());
        }
        if (builder.getCursor() != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(builder.getCursor()));
        }
        return fetchOptions;
    }

    private void setCursor(QueryBuilder<?> builder, QueryResultList<Entity> queryResult) {
        if (queryResult.getCursor() != null) {
            builder.setCursor(queryResult.getCursor().toWebSafeString());
        }
    }

    private PreparedQuery prepareQuery(QueryBuilder<?> builder, boolean keysOnly) throws FalsePredicateException {
        Query q = new Query(builder.getModel().getKind());

        if (keysOnly) {
            q.setKeysOnly();
        }

        prepareQueryAncestor(builder, q);
        prepareQueryWhere(builder, q);
        prepareQueryOrder(builder, q);

        return datastore().prepare(q);
    }

    private void prepareQueryOrder(QueryBuilder<?> builder, Query q) {
        if (builder.getPreOrders().isEmpty()) {
            return;
        }

        for (QueryOrder order : builder.getPreOrders()) {
            String string = getActualFieldName(order.getProperty(), builder.getModel().getClazz());
            q.addSort(string, getSortDirection(order));
        }
    }

    private void prepareQueryWhere(QueryBuilder<?> builder, Query q) throws FalsePredicateException {
        BaseCondition condition = builder.getCondition();
        if (condition != null && condition.hasPreFilter()) {
            q.setFilter(createFilter(builder, condition));
        }
    }

    private void prepareQueryAncestor(QueryBuilder<?> builder, Query q) {
        IdRef<?> parentId = builder.getParentId();
        if (parentId == null) {
            return;
        }
        q.setAncestor(IdRefToKey.toKey(r, parentId));
    }

    public SortDirection getSortDirection(QueryOrder order) {
        if (order.isDesc()) {
            return SortDirection.DESCENDING;
        }
        if (order.isAsc()) {
            return SortDirection.ASCENDING;
        }
        throw new RuntimeException("Invalid sort direction");
    }

    private Filter createFilter(QueryBuilder<?> builder, BaseCondition condition) throws FalsePredicateException {
        if (condition instanceof SimpleCondition) {
            return createSimpleFilter(builder, (SimpleCondition) condition);
        }
        if (condition instanceof JoinedCondition) {
            return createJoinedFilter(builder, (JoinedCondition) condition);
        }
        throw new RuntimeException("Invalid condition class: " + condition.getClass());
    }

    private Filter createSimpleFilter(QueryBuilder<?> builder, SimpleCondition condition) throws FalsePredicateException {
        String field = condition.getField();
        Class<?> clazz = builder.getModel().getClazz();
        Object whereValue = condition.getWhereValue();
        WhereOperator whereOperator = condition.getWhereOperator();

        String actualFieldName = getActualFieldName(field, clazz);
        Object actualValue = getActualFieldValue(field, clazz, whereValue);

        if (whereOperator == WhereOperator.IN && listSize(whereValue) == 0) {
            throw new FalsePredicateException();
        }

        return new FilterPredicate(actualFieldName, getFilterOperator(whereOperator), actualValue);
    }

    private Filter createJoinedFilter(QueryBuilder<?> builder, JoinedCondition joinedCondition) throws FalsePredicateException {
        BaseCondition[] conditions = joinedCondition.getConditions();
        LogicalOperator logicalOperator = joinedCondition.getLogicalOperator();

        List<Filter> filters = new ArrayList<>();
        for (int i = 0; i < conditions.length; i++) {
            try {
                BaseCondition condition = conditions[i];
                if (!condition.hasPreFilter()) {
                    continue;
                }

                filters.add(createFilter(builder, condition));
            } catch (FalsePredicateException e) {
                if (logicalOperator == LogicalOperator.AND) {
                    throw e;
                }
            }
        }

        if (filters.isEmpty()) {
            throw new FalsePredicateException();
        }

        if (filters.size() == 1) {
            return filters.get(0);
        }

        Filter[] filtersArray = filters.toArray(new Filter[filters.size()]);

        if (logicalOperator == LogicalOperator.AND) {
            return CompositeFilterOperator.and(filtersArray);
        }
        if (logicalOperator == LogicalOperator.OR) {
            return CompositeFilterOperator.or(filtersArray);
        }

        throw new RuntimeException("Invalid logical operator: " + logicalOperator);
    }

    private <T> String getActualFieldName(String fieldName, Class<T> clazz) {
        Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
        FieldModel fieldModel = new FieldModel(field);

        if (fieldModel.isId()) {
            return Entity.KEY_RESERVED_PROPERTY;
        }

        if (fieldModel.isIndexNormalizable()) {
            return NORMALIZED_FIELD_PREFIX + fieldName;
        }

        return fieldName;
    }

    public <T> Object getActualFieldValue(String fieldName, Class<T> clazz, Object value) {
        Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
        FieldModel fieldModel = new FieldModel(field);

        if (fieldModel.isCollection(value)) {
            return getActualListFieldValue(fieldName, clazz, (Collection<?>) value);
        }

        if (fieldModel.isId()) {
            return getActualKeyFieldValue(value);
        }

        if (fieldModel.isEnum(value)) {
            return value.toString();
        }

        if (fieldModel.isIndexNormalizable()) {
            return normalizeValue(value);
        }

        if (value instanceof IdRef) {
            return ((IdRef<?>) value).getUri();
        }

        if (fieldModel.isDate() && value instanceof String) {
            return DateUtils.toTimestamp((String) value);
        }

        return value;
    }

    private <T> Object getActualListFieldValue(String fieldName, Class<T> clazz, Collection<?> value) {
        Collection<?> objects = value;
        List<Object> values = new ArrayList<>();
        for (Object obj : objects) {
            values.add(getActualFieldValue(fieldName, clazz, obj));
        }
        return values;
    }

    private <T> Key getActualKeyFieldValue(Object value) {
        IdRef<?> id = (IdRef<?>) value;
        return IdRefToKey.toKey(r, id);
    }

    private FilterOperator getFilterOperator(WhereOperator whereOperator) {
        switch (whereOperator) {
            case EQUAL:
                return FilterOperator.EQUAL;
            case GREATER_THAN:
                return FilterOperator.GREATER_THAN;
            case GREATER_THAN_OR_EQUAL:
                return FilterOperator.GREATER_THAN_OR_EQUAL;
            case IN:
                return FilterOperator.IN;
            case LESS_THAN:
                return FilterOperator.LESS_THAN;
            case LESS_THAN_OR_EQUAL:
                return FilterOperator.LESS_THAN_OR_EQUAL;
            case NOT_EQUAL:
                return FilterOperator.NOT_EQUAL;
            default:
                throw new RuntimeException("Invalid where operator: " + whereOperator);
        }
    }

    private Object normalizeValue(Object o) {
        if (o == null) {
            return null;
        }

        if (!o.getClass().equals(String.class)) {
            return o;
        }

        return StringUtils.stripAccents((String) o).toLowerCase();
    }

    public int listSize(Object value) {
        if (value == null) {
            return 0;
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }
        if (Collection.class.isAssignableFrom(value.getClass())) {
            return Collection.class.cast(value).size();
        }
        if (Iterable.class.isAssignableFrom(value.getClass())) {
            return iterableSize(value);
        }
        throw new RuntimeException("Value used with operator 'in' is not an array or list.");
    }

    private int iterableSize(Object value) {
        Iterator<?> it = Iterable.class.cast(value).iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            i++;
        }
        return i;
    }

}
