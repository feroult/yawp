package io.yawp.servlet.rest;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.DatastoreQuery;
import io.yawp.repository.query.NoResultException;
import io.yawp.repository.response.HttpResponse;
import io.yawp.repository.response.JsonResponse;
import io.yawp.servlet.HttpException;
import io.yawp.utils.JsonUtils;

import java.util.Map;

public abstract class RestAction {

	protected static final String QUERY_OPTIONS = "q";

	protected static final String TRANSFORMER = "t";

	protected Repository r;

	protected boolean enableHooks;

	protected Class<?> clazz;

	protected IdRef<?> id;

	protected Map<String, String> params;

	public void setRepository(Repository r) {
		this.r = r;
	}

	public void setEnableHooks(boolean enableHooks) {
		this.enableHooks = enableHooks;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setId(IdRef<?> id) {
		this.id = id;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
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
			return r.queryWithHooks(clazz);
		}
		return r.query(clazz);
	}

}
