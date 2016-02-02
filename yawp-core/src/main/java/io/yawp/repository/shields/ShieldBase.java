package io.yawp.repository.shields;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.utils.FacadeUtils;
import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.InvalidActionMethodException;
import io.yawp.repository.query.condition.BaseCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ShieldBase<T> extends Feature {

    private ShieldRules<T> rules = new ShieldRules<>();

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

    // initialize methods

    public void setEndpointClazz(Class<?> endpointClazz) {
        this.endpointClazz = endpointClazz;
    }

    public final void setId(IdRef<?> id) {
        this.id = id;
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

    // shield rules

    public final Rule allow() {
        return allow(true);
    }

    public final Rule allow(boolean allow) {
        Rule rule = new Rule<T>(yawp, endpointClazz, id, objects);

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
        return yawp.getFeatures().getByClazz(endpointClazz).getActionClazz(actionKey);
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
        Class<? super T> facade = rules.getFacade();

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
        Class<? super T> facade = rules.getFacade();

        if (facade == null) {
            return;
        }

        FacadeUtils.get((T) object, facade);
    }

    @SuppressWarnings("unchecked")
    public final void setObjects(List<?> objects) {
        this.objects = (List<T>) objects;
    }

    public BaseCondition getWhere() {
        return rules.getWhere();
    }

    public boolean hasCondition() {
        return rules.hasCondition();
    }

    public boolean hasFacade() {
        return rules.hasFacade();
    }

    private void verifyConditions() {
        this.allow = rules.evaluateConditions();
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
