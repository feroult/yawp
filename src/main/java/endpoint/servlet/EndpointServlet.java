package endpoint.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import endpoint.repository.EndpointException;
import endpoint.repository.EndpointFeatures;
import endpoint.repository.EndpointScanner;
import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.RepositoryFeatures;
import endpoint.repository.actions.ActionKey;
import endpoint.repository.query.DatastoreQuery;
import endpoint.repository.query.DatastoreQueryOptions;
import endpoint.repository.query.NoResultException;
import endpoint.repository.response.ErrorResponse;
import endpoint.repository.response.HttpResponse;
import endpoint.repository.response.JsonResponse;
import endpoint.utils.EntityUtils;
import endpoint.utils.Environment;
import endpoint.utils.HttpVerb;
import endpoint.utils.JsonUtils;

//TODO remove dependence to EntityUtils, move EntityUtils inside repository
public class EndpointServlet extends HttpServlet {

	private static final long serialVersionUID = 8155293897299089610L;

	private RepositoryFeatures features;

	private boolean enableHooks = true;

	private boolean enableProduction = false;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		setWithHooks(config.getInitParameter("enableHooks"));
		setProduction(config.getInitParameter("enableProduction"));
		scanEndpoints(config.getInitParameter("packagePrefix"));
	}

	private void setProduction(String enableProductionParameter) {
		this.enableProduction = enableProductionParameter != null && Boolean.valueOf(enableProductionParameter);
	}

	private void setWithHooks(String enableHooksParameter) {
		this.enableHooks = enableHooksParameter == null || Boolean.valueOf(enableHooksParameter);
	}

	public EndpointServlet() {
	}

	protected EndpointServlet(String packagePrefix) {
		scanEndpoints(packagePrefix);
	}

	private void scanEndpoints(String packagePrefix) {
		features = new EndpointScanner(packagePrefix).enableHooks(enableHooks).scan();
	}

	private void response(HttpServletResponse resp, HttpResponse httpResponse) throws IOException {
		if (httpResponse == null) {
			new JsonResponse("{\"status\":\"ok\"}").execute(resp);
		} else {
			httpResponse.execute(resp);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// TODO create another servlet to use as fixtures
		if (!enableProduction && Environment.isProduction()) {
			response(resp, new ErrorResponse(403));
			return;
		}

		HttpResponse httpResponse;
		try {
			httpResponse = execute(req.getMethod(), getUri(req), JsonUtils.readJson(req.getReader()), makeParams(req));
		} catch (HttpException e) {
			httpResponse = new ErrorResponse(e.getHttpStatus(), e.getText());
		} catch (EndpointException e) {
			httpResponse = new ErrorResponse(403);
		}

		response(resp, httpResponse);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> makeParams(HttpServletRequest req) {
		Map<String, String> map = new HashMap<String, String>();

		Enumeration<String> e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			map.put(name, req.getParameter(name));
		}

		return map;
	}

	private String getUri(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getServletPath().length());
	}

	protected HttpResponse execute(String method, String uri, String requestJson, Map<String, String> params) {
		Repository r = getRepository(params);
		EndpointRouter router = EndpointRouter.parse(r, HttpVerb.fromString(method), uri);
		EndpointFeatures<?> endpoint = router.getEndpointFeatures();

		switch (router.getRESTActionType()) {
		case INDEX:
			return new JsonResponse(index(r, router.getIdRef(), endpoint, q(params), t(params)));
		case SHOW:
			return new JsonResponse(get(r, router.getIdRef(), endpoint, t(params)));
		case CREATE:
			return new JsonResponse(save(r, router.getIdRef(), endpoint, requestJson));
		case UPDATE:
			return new JsonResponse(update(r, router.getIdRef(), endpoint, requestJson));
		case CUSTOM:
			return action(r, router.getIdRef(), router.getEndpointClazz(), router.getCustomActionKey(), params);
		case DESTROY:
			if (router.getIdRef() == null) {
				throw new HttpException(501, "DELETE is not implemented for collections");
			}
			return new JsonResponse(delete(router.getIdRef(), endpoint));
		default:
			throw new IllegalArgumentException("Invalid datastore action");
		}
	}

	private String delete(IdRef<?> idRef, EndpointFeatures<?> endpoint) {
		Object o = idRef.fetch(endpoint.getClazz());
		idRef.delete();
		return JsonUtils.to(o);
	}

	private String q(Map<String, String> params) {
		return params == null ? null : params.get("q");
	}

	private String t(Map<String, String> params) {
		return params == null ? null : params.get("t");
	}

	protected Repository getRepository(Map<String, String> params) {
		return Repository.r().setFeatures(features);
	}

	private HttpResponse action(Repository r, IdRef<?> idRef, Class<?> clazz, ActionKey actionKey, Map<String, String> params) {
		return r.action(idRef, clazz, actionKey, params);
	}

	private String save(Repository r, IdRef<?> parentId, EndpointFeatures<?> endpoint, String json) {
		if (JsonUtils.isJsonArray(json)) {
			return saveFromArray(r, parentId, endpoint.getClazz(), json);
		} else {
			return saveFromObject(r, parentId, endpoint.getClazz(), json);
		}
	}

	private String update(Repository r, IdRef<?> id, EndpointFeatures<?> endpoint, String json) {
		assert !JsonUtils.isJsonArray(json);
		Object object = JsonUtils.from(r, json, endpoint.getClazz());
		EntityUtils.setId(object, id);
		saveProcessedObject(r, object);
		return JsonUtils.to(object);
	}

	private String saveFromObject(Repository r, IdRef<?> parentId, Class<?> clazz, String json) {
		Object object = JsonUtils.from(r, json, clazz);
		saveInRepository(r, object, parentId);
		return JsonUtils.to(object);
	}

	private String saveFromArray(Repository r, IdRef<?> parentId, Class<?> clazz, String json) {
		List<?> objects = JsonUtils.fromList(r, json, clazz);

		for (Object object : objects) {
			saveInRepository(r, object, parentId);
		}

		return JsonUtils.to(objects);
	}

	private String index(Repository r, IdRef<?> parentId, EndpointFeatures<?> endpoint, String q, String t) {
		DatastoreQuery<?> query = executeQueryInRepository(r, endpoint.getClazz());

		if (parentId != null) {
			query.from(parentId);
		}

		if (q != null) {
			query.options(DatastoreQueryOptions.parse(q));
		}

		if (t != null) {
			return JsonUtils.to(query.transform(t).list());
		}

		return JsonUtils.to(query.list());
	}

	private <T> String get(Repository r, IdRef<?> idRef, EndpointFeatures<T> features, String t) {
		try {
			DatastoreQuery<T> query = executeQueryInRepository(r, features.getClazz()).whereById("=", idRef);
			return JsonUtils.to(t == null ? query.only() : query.transform(t).only());
		} catch (NoResultException e) {
			throw new HttpException(404);
		}
	}

	private void saveInRepository(Repository r, Object object, IdRef<?> parentId) {
		EntityUtils.setParentId(object, parentId);
		saveProcessedObject(r, object);
	}

	private void saveProcessedObject(Repository r, Object object) {
		if (enableHooks) {
			r.saveWithHooks(object);
		} else {
			r.save(object);
		}
	}

	private <T> DatastoreQuery<T> executeQueryInRepository(Repository r, Class<T> clazz) {
		if (enableHooks) {
			return r.queryWithHooks(clazz);
		} else {
			return r.query(clazz);
		}
	}
}
