package io.yawp.servlet;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.RequestContext;
import io.yawp.commons.utils.Environment;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.*;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.servlet.rest.RestAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EndpointRouter {

    private Repository r;

    private RepositoryFeatures features;

    private String uri;

    private boolean overCollection;

    private ActionKey customActionKey;

    private HttpVerb verb;

    private IdRef<?> id;

    private Class<?> endpointClazz;

    private String requestJson;

    private Map<String, String> params;

    private List<?> objects;

    private EndpointRouter(Repository r, RequestContext ctx) {
        if (isWelcome(ctx.getUri())) {
            welcome(r);
        }

        if (isMeta(ctx.getUri())) {
            MetaHandler.handle(r, ctx.getJson());
        }

        this.r = r;
        this.verb = ctx.getHttpVerb();
        this.uri = ctx.getUri();
        this.requestJson = ctx.getJson();
        this.params = ctx.getParams();
        this.features = r.getFeatures();

        try {
            parseAll();
        } catch (EndpointNotFoundException e) {
            throw new HttpException(404, "Endpoint not found: " + e.getEndpointPath());
        }
    }

    private boolean isWelcome(String uri) {
        return uri.equals("") || uri.equals("/");
    }

    private boolean isMeta(String uri) {
        return uri.matches("^\\/?_meta(\\/.*)?$");
    }

    private void welcome(Repository r) {
        Welcome welcome = new Welcome();
        welcome.setMessage("Welcome to YAWP!");
        welcome.setVersion(Environment.version());
        welcome.setDriver(r.driver().name());
        throw new HttpException(200, JsonUtils.to(welcome));
    }

    public static EndpointRouter parse(Repository r, RequestContext ctx) {
        return new EndpointRouter(r, ctx);
    }

    private void parseAll() {
        this.id = IdRef.parse(r, verb, uri);

        this.customActionKey = parseCustomActionKey();
        this.overCollection = parseOverCollection();
        this.endpointClazz = parseEndpointClazz();

        if (!isCustomAction()) {
            this.objects = parseRequestJson();
        }
    }

    private Class<?> parseEndpointClazz() {
        String[] parts = uri.substring(1).split("/");

        if (isOverCollection()) {
            if (isCustomAction()) {
                return features.getByPath("/" + parts[parts.length - 2]).getClazz();
            }
            return features.getByPath("/" + parts[parts.length - 1]).getClazz();
        }

        return id.getClazz();
    }

    private ActionKey parseCustomActionKey() {

        if (id == null) {
            return rootCollectionCustomActionKey();
        }

        if (id.getUri().length() == uri.length()) {
            return null;
        }

        String lastToken = uri.substring(id.getUri().length() + 1);
        if (hasTwoParts(lastToken)) {
            return nestedCollectionCustomActionKey(lastToken);
        }

        return singleObjectCustomActionKey(lastToken);
    }

    private ActionKey singleObjectCustomActionKey(String lastToken) {
        ActionKey actionKey = new ActionKey(verb, lastToken, false);
        if (features.hasCustomAction(id.getClazz(), actionKey)) {
            return actionKey;
        }

        return null;
    }

    private ActionKey nestedCollectionCustomActionKey(String lastToken) {
        String[] tokens = lastToken.split("/");

        ActionKey actionKey = new ActionKey(verb, tokens[1], true);
        if (features.hasCustomAction("/" + tokens[0], actionKey)) {
            return actionKey;
        }
        return null;
    }

    private ActionKey rootCollectionCustomActionKey() {
        String[] tokens = uri.substring(1).split("/");

        if (tokens.length == 1) {
            return null;
        }

        ActionKey actionKey = new ActionKey(verb, tokens[1], true);
        if (features.hasCustomAction("/" + tokens[0], actionKey)) {
            return actionKey;
        }

        return null;
    }

    private boolean parseOverCollection() {
        if (id == null) {
            return true;
        }

        if (id.getUri().length() == uri.length()) {
            return false;
        }

        String lastToken = uri.substring(id.getUri().length() + 1);
        if (hasTwoParts(lastToken)) {
            return true;
        }

        ActionKey actionKey = new ActionKey(verb, lastToken, false);
        return !features.hasCustomAction(id.getClazz(), actionKey);

    }

    private boolean hasTwoParts(String lastToken) {
        return lastToken.contains("/");
    }

    public boolean isOverCollection() {
        return overCollection;
    }

    public boolean isCustomAction() {
        return customActionKey != null;
    }

    public String getCustomActionName() {
        if (!isCustomAction()) {
            return null;
        }
        return customActionKey.getActionName();
    }

    private RestAction createRestAction(boolean enableHooks) {
        try {
            Class<? extends RestAction> restActionClazz = RestAction.getRestActionClazz(verb, isOverCollection(), isCustomAction());

            RestAction action = restActionClazz.newInstance();

            action.setRepository(r);
            action.setEnableHooks(enableHooks);
            action.setEndpointClazz(endpointClazz);
            action.setId(id);
            action.setParams(params);
            action.setCustomActionKey(customActionKey);
            action.setRequestJson(requestJson);
            action.setObjects(objects);

            action.defineTrasnformer();
            action.defineShield();

            return action;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<?> parseRequestJson() {
        if (StringUtils.isBlank(requestJson)) {
            return null;
        }

        if (JsonUtils.isJsonArray(requestJson)) {
            return JsonUtils.fromList(r, requestJson, endpointClazz);
        }

        return Collections.singletonList(JsonUtils.from(r, requestJson, endpointClazz));
    }

    public HttpResponse executeRestAction(boolean enableHooks) {
        return createRestAction(enableHooks).execute();
    }

    public boolean isValid() {
        return tryToAdjustIds();
    }

    public boolean tryToAdjustIds() {
        if (objects == null) {
            return true;
        }

        for (Object object : objects) {
            IdRef<?> idInObject = forceIdInObjectIfNecessary(object);
            IdRef<?> parentIdInObject = forceParentIdInObjectIfNecessary(object, idInObject);

            if (idInObject == null) {
                if (parentIdInObject != null && id != null && !parentIdInObject.equals(id)) {
                    return false;
                }
                continue;
            }

            if (parentIdInObject != null && !parentIdInObject.equals(idInObject.getParentId())) {
                return false;
            }

            if (!idInObject.getClazz().equals(endpointClazz)) {
                return false;
            }

            if (id == null) {
                continue;
            }

            if (id.equals(idInObject)) {
                continue;
            }

            if (verb != HttpVerb.POST) {
                return false;
            }

            if (!id.isAncestorId(idInObject)) {
                return false;
            }
        }

        return true;
    }

    private IdRef<?> forceParentIdInObjectIfNecessary(Object object, IdRef<?> idInObject) {
        ObjectHolder objectHolder = new ObjectHolder(object);

        if (objectHolder.getModel().getParentClazz() == null) {
            return null;
        }

        IdRef<?> parentId = objectHolder.getParentId();
        if (parentId != null) {
            return parentId;
        }

        if (idInObject != null) {
            objectHolder.setParentId(idInObject.getParentId());
            return idInObject.getParentId();
        }

        if (id != null) {
            objectHolder.setParentId(id);
            return id;
        }

        return null;
    }

    private IdRef<?> forceIdInObjectIfNecessary(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);

        IdRef<?> idInObject = objectHolder.getId();

        if (idInObject != null) {
            return idInObject;
        }

        if (id != null && id.getClazz().equals(endpointClazz)) {
            objectHolder.setId(id);
            return id;
        }

        return null;
    }
}
