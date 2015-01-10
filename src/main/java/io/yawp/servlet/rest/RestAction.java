package io.yawp.servlet.rest;

import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.DatastoreQuery;
import io.yawp.repository.query.NoResultException;
import io.yawp.repository.response.HttpResponse;
import io.yawp.repository.response.JsonResponse;
import io.yawp.repository.transformers.RepositoryTransformers;
import io.yawp.servlet.HttpException;
import io.yawp.utils.JsonUtils;

import java.util.Map;

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

	public abstract Object action();

	public HttpResponse execute() {
		try {
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

}
