package io.yawp.repository.shields;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.utils.FacadeUtils;
import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;
import io.yawp.repository.Repository;
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

    private boolean allow = false;

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

    public final AllowRule allow() {
        return allow(true);
    }

    public final AllowRule allow(boolean allow) {
        AllowRule rule = new AllowRule<T>(yawp, endpointClazz, id, objects);

        if (allow) {
            rules.add(rule);
            this.allow = true;
        }

        return rule;
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
        Class<? super T> facade = getFacadeFromLastRule();

        if (facade == null) {
            return;
        }

        for (T object : objects) {
            ObjectHolder objectHolder = new ObjectHolder(object);
            IdRef<T> existingObjectId = (IdRef<T>) objectHolder.getId();

            if (existingObjectId == null) {
                FacadeUtils.set(object, facade);
                continue;
            }

            FacadeUtils.set(object, existingObjectId.fetch(), facade);
        }

    }

    @SuppressWarnings("unchecked")
    public void applyGetFacade(Object object) {
        Class<? super T> facade = getFacadeFromLastRule();

        if (facade == null) {
            return;
        }

        FacadeUtils.get((T) object, facade);
    }

    private Class<? super T> getFacadeFromLastRule() {
        if (rules.size() == 0) {
            return null;
        }
        return rules.get(rules.size() - 1).getFacade();
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
            if (!rule.hasConditions()) {
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
            if (rule.hasConditions()) {
                return true;
            }
        }

        return false;
    }

    private ShieldConditions getConditions() {
        ShieldConditions conditions = null;

        for (AllowRule rule : rules) {
            if (!rule.hasConditions()) {
                continue;
            }

            if (conditions == null) {
                conditions = rule.getConditions();
                continue;
            }

            conditions.or(rule.getConditions().getWhere());
        }

        return conditions;
    }

    private void verifyConditions() {
        if (!hasCondition()) {
            return;
        }
        this.allow = getConditions().evaluate();
    }

    public boolean hasFacade() {
        return getFacadeFromLastRule() != null;
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

}
