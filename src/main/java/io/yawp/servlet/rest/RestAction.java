package io.yawp.servlet.rest;

import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.JsonResponse;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.DatastoreQuery;
import io.yawp.repository.query.NoResultException;
import io.yawp.repository.shields.Shield;
import io.yawp.repository.shields.ShieldInfo;
import io.yawp.repository.transformers.RepositoryTransformers;
import io.yawp.servlet.HttpException;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public abstract class RestAction {

	private static final String DEFAULT_TRANSFORMER_NAME = "defaults";

	protected static final String QUERY_OPTIONS = "q";

	protected static final String TRANSFORMER = "t";

	protected Repository r;

	protected boolean enableHooks;

	protected Class<?> endpointClazz;

	protected IdRef<?> id;

	protected String requestJson;

	protected Map<String, String> params;

	protected ActionKey customActionKey;

	protected String actionName;

	protected String transformerName;

	protected Shield<?> shield;

	private List<?> objects;

	private Object object;

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

	public void setRequestJson(String requestJson) {
		this.requestJson = requestJson;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void setCustomActionKey(ActionKey customActionKey) {
		this.customActionKey = customActionKey;
	}

	public abstract void shield();

	public abstract Object action();

	public HttpResponse execute() {
		try {
			if (hasShield()) {
				shield();
			}

			Object object = action();

			if (HttpResponse.class.isInstance(object)) {
				return (HttpResponse) object;
			}

			return new JsonResponse(JsonUtils.to(object));

		} catch (NoResultException e) {
			throw new HttpException(404);
		}
	}

	protected DatastoreQuery<?> query() {
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
		if (!hasTransformer()) {
			return object;
		}

		return RepositoryTransformers.execute(r, object, getTransformerName());
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
			shield.setId(id);
			shield.setObject(object);
			shield.setObjects(objects);
			shield.setActionKey(customActionKey);
			shield.setActionMethods(shieldInfo.getActionMethods());
			return shield;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void parseJson() {
		if (StringUtils.isBlank(requestJson)) {
			return;
		}

		if (JsonUtils.isJsonArray(requestJson)) {
			objects = JsonUtils.fromList(r, requestJson, endpointClazz);
		} else {
			object = JsonUtils.from(r, requestJson, endpointClazz);
		}
	}

	protected boolean isJsonArray() {
		return objects != null;
	}

	public List<?> getObjects() {
		return objects;
	}

	public Object getObject() {
		return object;
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
		case PATCH:
			assertNotOverCollection(overCollection);
			return UpdateRestAction.class;
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
}
