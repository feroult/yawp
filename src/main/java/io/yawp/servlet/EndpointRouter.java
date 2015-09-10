package io.yawp.servlet;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.EndpointNotFoundException;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.actions.ActionKey;
import io.yawp.servlet.rest.RestAction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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

	private EndpointRouter(Repository r, HttpVerb verb, String uri, String requestJson, Map<String, String> params) {
		this.verb = verb;
		this.uri = uri;
		this.r = r;
		this.requestJson = requestJson;
		this.params = params;
		this.features = r.getFeatures();

		try {
			parseAll();
		} catch (EndpointNotFoundException e) {
			throw new HttpException(404, "Endpoint not found: " + e.getEndpointPath());
		}
	}

	public static EndpointRouter parse(Repository r, HttpVerb verb, String uri, String requestJson, Map<String, String> params) {
		return new EndpointRouter(r, verb, uri, requestJson, params);
	}

	private void parseAll() {
		this.id = IdRef.parse(r, verb, uri);

		this.customActionKey = parseCustomActionKey();
		this.overCollection = parseOverCollection();
		this.endpointClazz = parseEndpointClazz();
		this.objects = parseRequestJson();
	}

	private Class<?> parseEndpointClazz() {
		String[] parts = uri.substring(1).split("/");

		if (isOverCollection()) {
			if (isCustomAction()) {
				return features.get("/" + parts[parts.length - 2]).getClazz();
			}
			return features.get("/" + parts[parts.length - 1]).getClazz();
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
		if (features.hasCustomAction(id.getClazz(), actionKey)) {
			return false;
		}

		return true;
	}

	private boolean hasTwoParts(String lastToken) {
		return lastToken.indexOf("/") != -1;
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

	public ActionKey getCustomActionKey() {
		return customActionKey;
	}

	public EndpointFeatures<?> getEndpointFeatures() {
		return features.get(endpointClazz);
	}

	public Class<?> getEndpointClazz() {
		return getEndpointFeatures().getClazz();
	}

	public IdRef<?> getIdRef() {
		return id;
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

		return Arrays.asList(JsonUtils.from(r, requestJson, endpointClazz));
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
				if (parentIdInObject != null && !parentIdInObject.equals(id)) {
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
		// check if objectH can be created right from the routing start
		ObjectHolder objectH = new ObjectHolder(object);

		if (objectH.getModel().getParentClazz() == null) {
			return null;
		}

		IdRef<?> parentId = objectH.getParentId();
		if (parentId != null) {
			return parentId;
		}

		if (idInObject != null) {
			objectH.setParentId(idInObject.getParentId());
			return idInObject.getParentId();
		}

		if (id != null) {
			objectH.setParentId(id);
			return id;
		}

		return null;
	}

	private IdRef<?> forceIdInObjectIfNecessary(Object object) {
		ObjectHolder objectH = new ObjectHolder(object);
		IdRef<?> idInObject = objectH.getId();

		if (idInObject != null) {
			return idInObject;
		}

		if (id != null && id.getClazz().equals(endpointClazz)) {
			objectH.setId(id);
			return id;
		}

		return null;
	}
}
