package io.yawp.repository.shields;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.Condition;

import java.util.List;

public class AllowRule<T> {

    private final Repository r;

    private final Class<?> endpointClazz;

    private final IdRef<?> id;

    private final List<T> objects;

    private ShieldConditions conditions;

    private Class<? super T> facade;

    public AllowRule(Repository r, Class<?> endpointClazz, IdRef<?> id, List<T> objects) {

        this.r = r;
        this.endpointClazz = endpointClazz;
        this.id = id;
        this.objects = objects;
    }

    public boolean hasConditions() {
        return conditions != null;
    }

    public Class<? super T> getFacade() {
        return facade;
    }

    public boolean hasFacade() {
        return facade != null;
    }

    public AllowRule where(String field, String operator, Object value) {
        return or(Condition.c(field, operator, value));
    }

    public AllowRule where(BaseCondition condition) {
        return or(condition);
    }

    public AllowRule or(String field, String operator, Object value) {
        return or(Condition.c(field, operator, value));
    }

    public AllowRule or(BaseCondition condition) {
        getConditions().or(condition);
        return this;
    }

    public AllowRule and(String field, String operator, Object value) {
        return and(Condition.c(field, operator, value));
    }

    public AllowRule and(BaseCondition condition) {
        getConditions().and(condition);
        return this;
    }

    public AllowRule facade(Class<? super T> facade) {
        this.facade = facade;
        return this;
    }

    public ShieldConditions getConditions() {
        if (conditions != null) {
            return conditions;
        }

        conditions = new ShieldConditions(r, endpointClazz, id, objects);
        return conditions;
    }
}