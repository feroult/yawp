package io.yawp.repository.shields;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.utils.FacadeUtils;
import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.Condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ShieldBase<T> extends Feature {

    private List<AllowRule> rules = new ArrayList<>();

    private AllowRule lastRule;

    private boolean allow = false;

    private boolean lastAllow = false;

    private ShieldConditions conditions;

    private Class<? super T> facade;

    private Class<?> endpointClazz;

    private ActionKey actionKey;

    private Map<ActionKey, Method> actionMethods;

    protected IdRef<?> id;

    protected List<T> objects;

    protected String requestJson;

    protected Map<String, String> params;

    public abstract void always();

    public abstract void defaults();

    public abstract void index(IdRef<?> parentId);

    public abstract void show(IdRef<T> id);

    public abstract void create(List<T> objects);

    public abstract void update(IdRef<T> id, T object);

    public abstract void destroy(IdRef<T> id);

    public final ShieldBase<T> allow() {
        return allow(true);
    }

    public final ShieldBase<T> allow(boolean allow) {
        AllowRule rule = new AllowRule(allow, this);
        rules.add(rule);
        this.lastRule = rule;

        this.allow = this.allow || allow;
        this.lastAllow = allow;
        return this;
    }


    public final ShieldBase<T> where(String field, String operator, Object value) {
        return lastRule.or(Condition.c(field, operator, value));
    }

    public final ShieldBase<T> where(BaseCondition condition) {
        return lastRule.or(condition);
    }

    public final ShieldBase<T> or(String field, String operator, Object value) {
        return lastRule.or(Condition.c(field, operator, value));
    }

    public final ShieldBase<T> or(BaseCondition condition) {
        return lastRule.or(condition);
//        if (!lastAllow) {
//            return this;
//        }
//
//        getConditions().or(condition);
//        return this;
    }

    public final ShieldBase<T> and(String field, String operator, Object value) {
        return lastRule.and(Condition.c(field, operator, value));
    }

    public final ShieldBase<T> and(BaseCondition condition) {
        return lastRule.and(condition);
//        if (!lastAllow) {
//            return this;
//        }
//
//        getConditions().and(condition);
//        return this;
    }

    public final ShieldBase<T> facade(Class<? super T> facade) {
        return lastRule.facade(facade);
//        if (!lastAllow) {
//            return this;
//        }
//
//        this.facade = facade;
//        return this;
    }

    @Deprecated
    public final ShieldBase<T> removeFacade() {
        return facade(null);
    }

    public final boolean isAction(Class<?>... actionClazzes) {
        return isActionRoute() && Arrays.asList(actionClazzes).contains(currentActionClazz());
    }

    private boolean isActionRoute() {
        return actionKey != null;
    }

    private Class<?> currentActionClazz() {
        return yawp.getFeatures().get(endpointClazz).getActionClazz(actionKey);
    }

    public final boolean requestHasAnyObject() {
        return objects != null;
    }

    public final void protectIndex() {
        always();
        index(id);
        throwNotFoundIfNotAllowed();
    }

    @SuppressWarnings("unchecked")
    public final void protectShow() {
        always();
        show((IdRef<T>) id);
        throwNotFoundIfNotAllowed();
    }

    public final void protectCreate() {
        always();
        create(objects);
        throwNotFoundIfNotAllowed();

        applySetFacade();

        verifyConditions();
        throwForbiddenIfNotAllowed();
    }

    @SuppressWarnings("unchecked")
    public final void protectUpdate() {
        always();
        update((IdRef<T>) id, objects == null ? null : objects.get(0));
        throwNotFoundIfNotAllowed();

        applySetFacade();

        verifyConditions();
        throwForbiddenIfNotAllowed();
    }

    @SuppressWarnings("unchecked")
    public final void protectDestroy() {
        always();
        destroy((IdRef<T>) id);
        throwNotFoundIfNotAllowed();

        verifyConditions();
        throwForbiddenIfNotAllowed();
    }

    public final void protectCustom() {
        always();
        protectEachCustomAction();
        throwNotFoundIfNotAllowed();

        verifyConditions();
        throwForbiddenIfNotAllowed();
    }

    private void throwNotFoundIfNotAllowed() {
        if (!allow) {
            throw new HttpException(404, "The resquest was not allowed by the endpoint shield " + getClass().getName());
        }
    }

    private void throwForbiddenIfNotAllowed() {
        if (!allow) {
            throw new HttpException(403, "The resquest was not allowed by the endpoint shield " + getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private void applySetFacade() {
        Class<? super T> facadeX = getFacadeFromRules();

        if (facadeX == null) {
            return;
        }

        for (T object : objects) {
            ObjectHolder objectHolder = new ObjectHolder(object);
            IdRef<T> existingObjectId = (IdRef<T>) objectHolder.getId();

            if (existingObjectId == null) {
                FacadeUtils.set(object, facadeX);
                continue;
            }

            FacadeUtils.set(object, existingObjectId.fetch(), facadeX);
        }

    }

    @SuppressWarnings("unchecked")
    public void applyGetFacade(Object object) {

        Class<? super T> facadeX = getFacadeFromRules();

        if (facadeX == null) {
            return;
        }

        FacadeUtils.get((T) object, facadeX);
    }

    private Class<? super T> getFacadeFromRules() {
        Class<? super T> facadeX = null;

        for (AllowRule rule : rules) {
            if (!rule.isAllow() || !rule.hasFacade()) {
                continue;
            }
            facadeX = rule.getFacade();
        }
        return facadeX;
    }

    public void setEndpointClazz(Class<?> endpointClazz) {
        this.endpointClazz = endpointClazz;
    }

    public final void setId(IdRef<?> id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public final void setObjects(List<?> objects) {
        this.objects = (List<T>) objects;
    }

    public BaseCondition getCondition() {
        if (!hasCondition()) {
            return null;
        }

        BaseCondition where = null;

        for (AllowRule rule : rules) {
            if (!rule.isAllow() || !rule.hasConditions()) {
                continue;
            }

            if (where == null) {
                where = rule.getConditions().getWhere();
                continue;
            }

            where = where.or(rule.getConditions().getWhere());
        }

        return where;
    }

    public boolean hasCondition() {

        for (AllowRule rule : rules) {
            if (rule.isAllow()) {
                if (rule.hasConditions()) {
                    return true;
                }
            }
        }

        return false;

        //return getConditions().getWhere() != null;
    }

    private ShieldConditions getConditions() {
        ShieldConditions conditions = null;

        for (AllowRule rule : rules) {
            if (!rule.isAllow() || !rule.hasConditions()) {
                continue;
            }

            if (conditions == null) {
                conditions = rule.getConditions();
                continue;
            }

            conditions.or(rule.getConditions().getWhere());

        }

        return conditions;

//        if (conditions != null) {
//            return conditions;
//        }
//
//        conditions = new ShieldConditions(yawp, endpointClazz, id, objects);
//        return conditions;
    }

    private void verifyConditions() {
        if (!hasCondition()) {
            this.allow = true;
            return;
        }
        this.allow = getConditions().evaluate();
    }

    public boolean hasFacade() {
        return getFacadeFromRules() != null;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public final void setParams(Map<String, String> params) {
        this.params = params;
    }

    public final void setActionKey(ActionKey actionKey) {
        this.actionKey = actionKey;
    }

    public final void setActionMethods(Map<ActionKey, Method> actionMethods) {
        this.actionMethods = actionMethods;
    }

    private void protectEachCustomAction() {
        if (!actionMethods.containsKey(actionKey)) {
            defaults();
            return;
        }

        Method method = actionMethods.get(actionKey);
        invokeCustomActionShield(method);
    }

    private void invokeCustomActionShield(Method method) {
        try {
            method.invoke(this, createArguments(method));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] createArguments(Method method) {
        try {
            ActionMethod actionMethod = new ActionMethod(method);
            return actionMethod.createArguments(yawp, id, requestJson, params);
        } catch (InvalidActionMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public class AllowRule {

        private boolean allow;
        private ShieldBase<T> shieldBase;

        private ShieldConditions conditions;

        private Class<? super T> facade;

        public AllowRule(boolean allow, ShieldBase<T> shieldBase) {
            this.allow = allow;
            this.shieldBase = shieldBase;
        }

        public boolean isAllow() {
            return allow;
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

        public final ShieldBase<T> where(String field, String operator, Object value) {
            return or(Condition.c(field, operator, value));
        }

        public final ShieldBase<T> where(BaseCondition condition) {
            return or(condition);
        }

        public final ShieldBase<T> or(String field, String operator, Object value) {
            return or(Condition.c(field, operator, value));
        }

        public final ShieldBase<T> or(BaseCondition condition) {
            getConditions().or(condition);
            return shieldBase;
        }

        public final ShieldBase<T> and(String field, String operator, Object value) {
            return and(Condition.c(field, operator, value));
        }

        public final ShieldBase<T> and(BaseCondition condition) {
            getConditions().and(condition);
            return shieldBase;
        }

        public final ShieldBase<T> facade(Class<? super T> facade) {
            this.facade = facade;
            return shieldBase;
        }

        public final ShieldBase<T> removeFacade() {
            return facade(null);
        }

        private ShieldConditions getConditions() {
            if (conditions != null) {
                return conditions;
            }

            conditions = new ShieldConditions(yawp, endpointClazz, id, objects);
            return conditions;
        }
    }


}
