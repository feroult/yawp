package endpoint;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;

import endpoint.actions.RepositoryActions;
import endpoint.hooks.RepositoryHooks;
import endpoint.response.ErrorResponse;
import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.transformers.RepositoryTransformers;
import endpoint.utils.JsonUtils;

public class DatastoreServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(DatastoreServlet.class.getSimpleName());

	private static final long serialVersionUID = 8155293897299089610L;

	private Map<String, Class<?>> endpoints = new HashMap<String, Class<?>>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String packagePrefix = config.getInitParameter("packagePrefix");
		bootEndpoint(packagePrefix);
		scanEndpoints(packagePrefix);
	}

	public DatastoreServlet() {
	}

	protected DatastoreServlet(String packagePrefix) {
		scanEndpoints(packagePrefix);
	}

	private void bootEndpoint(String packagePrefix) {
		RepositoryActions.scan(packagePrefix);
		RepositoryHooks.scan(packagePrefix);
		RepositoryTransformers.scan(packagePrefix);
	}

	private void scanEndpoints(String packagePrefix) {
		Reflections reflections = new Reflections(packagePrefix);
		Set<Class<?>> clazzes = reflections.getTypesAnnotatedWith(Endpoint.class);

		for (Class<?> endpoint : clazzes) {
			Endpoint annotation = endpoint.getAnnotation(Endpoint.class);
			endpoints.put(annotation.path(), (Class<?>) endpoint);
		}
	}

	private void response(HttpServletResponse resp, HttpResponse httpResponse) throws IOException {
		if (httpResponse == null) {
			JsonResponse jsonResponse = new JsonResponse("{\"status\":\"ok\"}");
			jsonResponse.execute(resp);
			return;
		}
		httpResponse.execute(resp);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpResponse httpResponse;
		try {

			httpResponse = execute(req.getMethod(), getPath(req), JsonUtils.readJson(req.getReader()), makeParams(req));

		} catch (HttpException e) {
			httpResponse = new ErrorResponse(e.getHttpStatus(), e.getText());
		} catch (DatastoreException e) {
			httpResponse = new ErrorResponse(403);
		}

		response(resp, httpResponse);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, String> makeParams(HttpServletRequest req) {
		Map<String, String> map = new HashMap<String, String>();

		Enumeration e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			map.put(name, req.getParameter(name));
		}

		return map;
	}

	private String getPath(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getServletPath().length());
	}

	protected HttpResponse execute(String method, String path, String requestJson, Map<String, String> params) {
		DatastoreRouter router = new DatastoreRouter(method, path);
		Class<?> clazz = endpoints.get(router.getEndpointPath());

		Endpoint endpoint = clazz.getAnnotation(Endpoint.class);

		Repository r = getRepository(params);

		switch (router.getAction()) {
			case INDEX:
				if (!endpoint.index()) {
					return new ErrorResponse(403);
				}
				return new JsonResponse(index(r, clazz, q(params), t(params)));
			case SHOW:
				try {
					return new JsonResponse(get(r, clazz, router.getId(), t(params)));
				} catch (NoResultException e) {
					return new ErrorResponse(404);
				}
			case CREATE:
				return new JsonResponse(save(r, clazz, requestJson));
			case UPDATE:
				if (!endpoint.update()) {
					return new ErrorResponse(403);
				}
				return new JsonResponse(save(r, clazz, requestJson));
			case CUSTOM:
				return action(r, clazz, router.getMethod(), router.getCustomAction(), router.getId(), params);
		}

		throw new IllegalArgumentException("Invalid datastore action");
	}

	private String q(Map<String, String> params) {
		return params == null ? null : params.get("q");
	}

	private String t(Map<String, String> params) {
		return params == null ? null : params.get("t");
	}

	protected Repository getRepository(Map<String, String> params) {
		return Repository.r();
	}

	private HttpResponse action(Repository r, Class<?> clazz, String method, String customAction, Long id, Map<String, String> params) {
		return r.action(clazz, method, customAction, id, params);
	}

	private String save(Repository r, Class<?> clazz, String json) {
		logger.warning("JSON: " + json);

		if (JsonUtils.isJsonArray(json)) {
			return saveFromArray(r, clazz, json);
		} else {
			return saveFromObject(r, clazz, json);
		}
	}

	private String saveFromObject(Repository r, Class<?> clazz, String json) {
		Object object = JsonUtils.from(json, clazz);

		r.save(object);

		return JsonUtils.to(object);
	}

	private String saveFromArray(Repository r, Class<?> clazz, String json) {
		List<?> objects = JsonUtils.fromList(json, clazz);

		for (Object object : objects) {
			r.save(object);
		}

		return JsonUtils.to(objects);
	}

	private String index(Repository r, Class<?> clazz, String q, String t) {
		DatastoreQuery<?> query = r.queryWithHooks(clazz);

		if (q != null) {
			query.options(DatastoreQueryOptions.parse(q));
		}

		if (t != null) {
			return JsonUtils.to(query.transform(t).list());
		}

		return JsonUtils.to(query.list());
	}

	private <T> String get(Repository r, Class<T> clazz, Long id, String t) {
		DatastoreQuery<T> query = r.query(clazz).whereById("=", id);
		RepositoryHooks.beforeQuery(r, query, clazz);
		return JsonUtils.to(t == null ? query.only() : query.transform(t).only());
	}

}
