package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.driver.postgresql.IdRefToKey;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.IdRef;
import io.yawp.repository.Namespace;
import io.yawp.repository.Repository;
import io.yawp.repository.models.FieldModel;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Query {

    private static final String SQL_PREFIX = "select key, properties from :kind";

    private Repository r;

    private QueryBuilder<?> builder;

    // TODO: select only keys, today we fetch the entire row from database
    private boolean keysOnly;

    private Map<String, Object> whereBinds = new HashMap<String, Object>();

    public Query(QueryBuilder<?> builder, boolean keysOnly) {
        this.builder = builder;
        this.keysOnly = keysOnly;
        this.r = builder.getRepository();
    }

    public List<Entity> execute(ConnectionManager connectionManager) throws FalsePredicateException {
        List<Entity> entities = connectionManager.executeQuery(createRunner());
        setCursor(entities);
        return entities;
    }

    private void setCursor(List<Entity> entities) {
        if (entities.isEmpty()) {
            return;
        }

        long previousCursor = builder.getCursor() != null ? Long.valueOf(builder.getCursor()) : 0;
        builder.setCursor(String.valueOf(previousCursor + entities.size()));
    }

    private SqlRunner createRunner() throws FalsePredicateException {

        String sql = SQL_PREFIX + where() + order() + offset() + limit();

        return new DatastoreSqlRunner(getKind(), sql) {
            @Override
            protected void bind() {
                for (String key : whereBinds.keySet()) {
                    bind(key, whereBinds.get(key));
                }
            }

            @Override
            protected Object collect(ResultSet rs) throws SQLException {
                return getEntities(rs);
            }

        };
    }

    private String getKind() {
        return builder.getModel().getKind();
    }

    private String where() throws FalsePredicateException {
        if (!hasAnyKindOfFilter()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" where ");

        sb.append(whereNamespace());

        if (hasPropertyFilter()) {
            sb.append(" and ");
            sb.append(where(builder.getCondition()));
        }

        if (hasAncestorFilter()) {
            sb.append(" and ");
            sb.append(whereAncestor());
        }

        return sb.toString();
    }

    private boolean hasAnyKindOfFilter() {
        return hasPropertyFilter() || hasAncestorFilter();
    }

    private boolean hasAncestorFilter() {
        return builder.getParentId() != null;
    }

    private boolean hasPropertyFilter() {
        return builder.getCondition() != null && builder.getCondition().hasPreFilter();
    }

    private String bindValue(Object value) {
        String placeHolder = "p" + (whereBinds.size() + 1);
        whereBinds.put(placeHolder, value);
        return placeHolder;
    }

    private String whereNamespace() {
        String ns = NamespaceManager.get();

        if (Namespace.GLOBAL.equals(ns)) {
            return whereGlobaleNamespace();
        }

        return whereNamespace(ns);
    }

    private String whereNamespace(String ns) {
        String placeHolder = bindValue(ns);
        return String.format("key->>'ns' = :%s", placeHolder);
    }

    private String whereGlobaleNamespace() {
        return "(key->'ns') is null";
    }

    private String whereAncestor() {
        IdRef<?> parentId = builder.getParentId();
        String placeHolder = bindValue(IdRefToKey.toKey(r, parentId));
        return String.format("key%s = :%s", ancetorLink(parentId), placeHolder);
    }

    private String ancetorLink(IdRef<?> parentId) {
        StringBuilder sb = new StringBuilder();
        int ancestorNumber = getAncetorNumber(parentId);
        for (int i = -1; i < ancestorNumber; i++) {
            sb.append("->'parent'");
        }
        String ancestorLink = sb.toString();
        return ancestorLink;
    }

    protected int getAncetorNumber(IdRef<?> parentId) {
        return builder.getModel().getAncestorNumber(parentId.getClazz());
    }

    private String where(BaseCondition condition) throws FalsePredicateException {
        if (condition instanceof SimpleCondition) {
            return simpleWhere((SimpleCondition) condition);
        }
        if (condition instanceof JoinedCondition) {
            return joinedWhere((JoinedCondition) condition);
        }
        throw new RuntimeException("Invalid condition class: " + condition.getClass());
    }

    private String simpleWhere(SimpleCondition condition) throws FalsePredicateException {
        String fieldName = condition.getField();
        Class<?> clazz = builder.getModel().getClazz();
        Object whereValue = condition.getWhereValue();
        WhereOperator whereOperator = condition.getWhereOperator();

        String actualFieldName = getActualFieldName(fieldName, clazz);
        Object actualValue = getActualFieldValue(fieldName, clazz, whereValue);

        if (whereOperator == WhereOperator.IN) {
            if (listSize(whereValue) == 0) {
                throw new FalsePredicateException();
            }
            return whereCollectionValue(fieldName, actualFieldName, whereOperator, (Collection<?>) actualValue);
        }

        return whereSingleValue(fieldName, actualFieldName, whereOperator, actualValue);
    }

    private String whereCollectionValue(String fieldName, String actualFieldName, WhereOperator whereOperator, Collection<?> collection) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("(");
        for (Object actualValue : collection) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }

            String placeHolder = bindValue(actualValue);
            sb.append(":");
            sb.append(placeHolder);
        }
        sb.append(")");

        return String.format("%s %s %s", propertyLink(fieldName, actualFieldName), filterOperatorAsText(whereOperator), sb.toString());
    }

    private String whereSingleValue(String fieldName, String actualFieldName, WhereOperator whereOperator, Object actualValue) {
        if (isNull(whereOperator, actualValue)) {
            return whereSingleValueIsNull(actualFieldName);
        }
        String placeHolder = bindValue(actualValue);

        if (isList(fieldName)) {
            return String.format(":%s %s ANY(ARRAY(select * from json_array_elements_text(to_json(%s))))", placeHolder, filterOperatorAsText(whereOperator.reverse()), propertyLink(fieldName, actualFieldName, false));
        }
        return String.format("%s %s :%s", propertyLink(fieldName, actualFieldName), filterOperatorAsText(whereOperator), placeHolder);
    }

    private String whereSingleValueIsNull(String actualFieldName) {
        return String.format("(properties->'%s') is null", actualFieldName);
    }

    private boolean isNull(WhereOperator whereOperator, Object actualValue) {
        return whereOperator.equals(WhereOperator.EQUAL) && actualValue == null;
    }

    private boolean isList(String fieldName) {
        FieldModel fieldModel = builder.getModel().getFieldModel(fieldName);
        return fieldModel.isList();
    }

    private String propertyLink(String fieldName, String actualFieldName) {
        return propertyLink(fieldName, actualFieldName, true);
    }

    private String propertyLink(String fieldName, String actualFieldName, boolean scalar) {
        FieldModel fieldModel = builder.getModel().getFieldModel(fieldName);

        String retrieveOperator = scalar ? ">>" : ">";

        if (fieldModel.isId()) {
            return "key";
        }

        if (fieldModel.isNumber()) {
            return String.format("cast(properties-%s'%s' as numeric)", retrieveOperator, actualFieldName);
        }

        return String.format("properties-%s'%s'", retrieveOperator, actualFieldName);
    }

    private String joinedWhere(JoinedCondition joinedCondition) throws FalsePredicateException {
        BaseCondition[] conditions = joinedCondition.getConditions();
        LogicalOperator logicalOperator = joinedCondition.getLogicalOperator();

        List<String> wheres = new ArrayList<>();
        for (int i = 0; i < conditions.length; i++) {
            try {
                BaseCondition condition = conditions[i];
                if (!condition.hasPreFilter()) {
                    continue;
                }

                wheres.add(where(condition));
            } catch (FalsePredicateException e) {
                if (logicalOperator == LogicalOperator.AND) {
                    throw e;
                }
            }
        }

        if (wheres.isEmpty()) {
            throw new FalsePredicateException();
        }

        if (wheres.size() == 1) {
            return wheres.get(0);
        }

        return applyLogicalOperator(logicalOperator, wheres);
    }

    private String order() {
        if (CollectionUtils.isEmpty(builder.getPreOrders())) {
            return "";
        }

        Class<?> clazz = builder.getModel().getClazz();

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        sb.append(" order by ");

        for (QueryOrder order : builder.getPreOrders()) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }

            sb.append(String.format("properties->>'%s'", getActualFieldName(order.getProperty(), clazz)));

            if (order.isDesc()) {
                sb.append(" desc");
            }
        }

        return sb.toString();
    }

    private String offset() {
        if (builder.getCursor() == null) {
            return "";
        }
        return String.format(" offset %s", builder.getCursor());
    }

    private String limit() {
        if (builder.getLimit() == null) {
            return "";
        }
        return String.format(" limit %d", builder.getLimit());
    }

    private String applyLogicalOperator(LogicalOperator logicalOperator, List<String> wheres) {
        boolean first = true;
        String operator = logicalOperatorAsText(logicalOperator);

        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (String where : wheres) {
            if (!first) {
                sb.append(operator);
            } else {
                first = false;
            }
            sb.append(where);
        }
        sb.append(")");

        return sb.toString();
    }

    private <T> String getActualFieldName(String fieldName, Class<T> clazz) {
        Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
        FieldModel fieldModel = new FieldModel(field);

        if (fieldModel.isId()) {
            return Entity.KEY_RESERVED_PROPERTY;
        }

        if (fieldModel.isIndexNormalizable()) {
            return Entity.NORMALIZED_FIELD_PREFIX + fieldName;
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
            return getActualKeyFieldValue(clazz, value);
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
        Collection<?> objects = (Collection<?>) value;
        List<Object> values = new ArrayList<>();
        for (Object obj : objects) {
            values.add(getActualFieldValue(fieldName, clazz, obj));
        }
        return values;
    }

    private <T> Key getActualKeyFieldValue(Class<T> clazz, Object value) {
        IdRef<?> id = (IdRef<?>) value;
        return IdRefToKey.toKey(r, id);
    }

    private String logicalOperatorAsText(LogicalOperator logicalOperator) {
        String operator;
        if (logicalOperator == LogicalOperator.AND) {
            operator = " and ";
        } else if (logicalOperator == LogicalOperator.OR) {
            operator = " or ";
        } else {
            throw new RuntimeException("Invalid logical operator: " + logicalOperator);
        }
        return operator;
    }

    private String filterOperatorAsText(WhereOperator whereOperator) {
        switch (whereOperator) {
            case EQUAL:
                return "=";
            case GREATER_THAN:
                return ">";
            case GREATER_THAN_OR_EQUAL:
                return ">=";
            case IN:
                return "in";
            case LESS_THAN:
                return "<";
            case LESS_THAN_OR_EQUAL:
                return "<=";
            case NOT_EQUAL:
                return "<>";
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
