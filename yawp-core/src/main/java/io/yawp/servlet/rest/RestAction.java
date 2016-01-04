package io.yawp.servlet.rest;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.JsonResponse;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.shields.ShieldInfo;
import io.yawp.repository.transformers.RepositoryTransformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RestAction {

    private static final String DEFAULT_TRANSFORMER_NAME = "defaults";

    protected static final String QUERY_OPTIONS = "q";

    protected static final String TRANSFORMER = "t";

    protected Repository r;

    protected boolean enableHooks;

    protected Class<?> endpointClazz;

    protected IdRef<?> id;

    protected Map<String, String> params;

    protected ActionKey customActionKey;

    protected String actionName;

    protected String transformerName;

    protected Shield<?> shield;

    private List<?> objects;

    protected String requestJson;

    private boolean requestBodyJsonArray;

    public RestAction(String actionName) {
        this.actionName = actionName;
    }

    public void setRepository(Repository r) {
        this.r = r;
    }

    public void setEnableHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
    }

    public void setEndpointClazz(Class<?> clazz) {
        this.endpointClazz = clazz;
    }

    public void setId(IdRef<?> id) {
        this.id = id;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setCustomActionKey(ActionKey customActionKey) {
        this.customActionKey = customActionKey;
    }

    public boolean isRequestBodyJsonArray() {
        return requestBodyJsonArray;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
        this.requestBodyJsonArray = JsonUtils.isJsonArray(requestJson);
    }

    public void setRequestBodyJsonArray(boolean requestBodyJsonArray) {
        this.requestBodyJsonArray = requestBodyJsonArray;
    }

    protected void beforeShield() {
        if (objects == null) {
            return;
        }
        for (Object object : objects) {
            RepositoryHooks.beforeShield(r, object);
        }
    }

    public abstract void shield();

    public abstract Object action();

    public HttpResponse execute() {

        executeShield();

        Object object = action();

        if (HttpResponse.class.isInstance(object)) {
            return (HttpResponse) object;
        }

        return new JsonResponse(JsonUtils.to(object));
    }

    private void executeShield() {
        if (!enableHooks) {
            return;
        }

        beforeShield();

        if (hasShield()) {
            shield();
        }
    }

    protected QueryBuilder<?> query() {
        if (enableHooks) {
            return r.queryWithHooks(endpointClazz);
        }
        return r.query(endpointClazz);
    }

    protected void save(Object object) {
        if (enableHooks) {
            r.saveWithHooks(object);
        } else {
            r.save(object);
        }
    }

    protected FutureObject<Object> saveAsync(Object object) {
        if (enableHooks) {
            return r.async().saveWithHooks(object);
        } else {
            return r.async().save(object);
        }
    }

    protected Object transform(Object object) {
        if (isList(object)) {
            return transform((List<?>) object);
        }

        if (!hasTransformer() || !object.getClass().equals(endpointClazz)) {
            return object;
        }

        return RepositoryTransformers.execute(r, object, getTransformerName());
    }

    protected Object transform(List<?> objects) {
        if (!hasTransformer()) {
            return objects;
        }

        List<Object> result = new ArrayList<Object>();
        for (Object object : objects) {
            if (!hasTransformer() || !object.getClass().equals(endpointClazz)) {
                result.add(object);
                continue;
            }
            result.add(RepositoryTransformers.execute(r, object, getTransformerName()));
        }

        return result;
    }

    protected void applyGetFacade(Object object) {
        if (isList(object)) {
            applyGetFacade((List<?>) object);
        }

        if (!hasFacade()) {
            return;
        }
        shield.applyGetFacade(object);
    }

    protected void applyGetFacade(List<?> objects) {
        if (!hasFacade()) {
            return;
        }

        for (Object object : objects) {
            shield.applyGetFacade(object);
        }
    }

    protected String getTransformerName() {
        return transformerName;
    }

    protected boolean hasTransformer() {
        return transformerName != null;
    }

    public void defineTrasnformer() {
        if (params.containsKey(TRANSFORMER)) {
            transformerName = params.get(TRANSFORMER);
            return;
        }
        if (r.getEndpointFeatures(endpointClazz).hasTranformer(actionName)) {
            transformerName = actionName;
            return;
        }
        if (r.getEndpointFeatures(endpointClazz).hasTranformer(DEFAULT_TRANSFORMER_NAME)) {
            transformerName = DEFAULT_TRANSFORMER_NAME;
            return;
        }
    }

    protected boolean hasShield() {
        return shield != null;
    }

    protected boolean hasFacade() {
        return shield != null && shield.hasFacade();
    }

    protected boolean hasShieldCondition() {
        return hasShield() && shield.hasCondition();
    }

    public void defineShield() {
        EndpointFeatures<?> endpointFeatures = r.getEndpointFeatures(endpointClazz);
        if (endpointFeatures.hasShield()) {
            shield = createShield(endpointFeatures);
        }
    }

    private Shield<?> createShield(EndpointFeatures<?> endpointFeatures) {
        try {
            ShieldInfo<?> shieldInfo = endpointFeatures.getShieldInfo();

            Shield<?> shield = shieldInfo.getShieldClazz().newInstance();
            shield.setRepository(r);
            shield.setEndpointClazz(endpointClazz);
            shield.setId(id);
            shield.setObjects(objects);
            shield.setRequestJson(requestJson);
            shield.setParams(params);
            shield.setActionKey(customActionKey);
            shield.setActionMethods(shieldInfo.getActionMethods());
            return shield;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setObjects(List<?> objects) {
        this.objects = objects;
    }

    public List<?> getObjects() {
        return requestBodyJsonArray ? objects : null;
    }

    public Object getObject() {
        return objects == null || requestBodyJsonArray ? null : objects.get(0);
    }

    public static Class<? extends RestAction> getRestActionClazz(HttpVerb verb, boolean overCollection, boolean isCustomAction) {
        if (isCustomAction) {
            return CustomRestAction.class;
        }

        switch (verb) {
            case GET:
                return overCollection ? IndexRestAction.class : ShowRestAction.class;
            case POST:
                return CreateRestAction.class;
            case PUT:
                assertNotOverCollection(overCollection);
                return UpdateRestAction.class;
            case PATCH:
                assertNotOverCollection(overCollection);
                return PatchRestAction.class;
            case DELETE:
                assertNotOverCollection(overCollection);
                return DestroyRestAction.class;
            case OPTIONS:
                return RoutesRestAction.class;
        }
        throw new HttpException(501, "Unsuported http verb " + verb);
    }

    private static void assertNotOverCollection(boolean overCollection) {
        if (overCollection) {
            throw new HttpException(501);
        }
    }

    private boolean isList(Object object) {
        return List.class.isAssignableFrom(object.getClass());
    }
}
