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
import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;

public class DatastoreServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(DatastoreServlet.class.getSimpleName());

	private static final long serialVersionUID = 8155293897299089610L;

	private Map<String, Class<? extends DatastoreObject>> endpoints = new HashMap<String, Class<? extends DatastoreObject>>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String packagePrefix = config.getInitParameter("packagePrefix");
		scanActionsAndHooks(packagePrefix);
		scanEndpoints(packagePrefix);
	}

	public DatastoreServlet() {
	}

	protected DatastoreServlet(String packagePrefix) {
		scanEndpoints(packagePrefix);
	}

	private void scanActionsAndHooks(String packagePrefix) {
		RepositoryActions.scan(packagePrefix);
		RepositoryHooks.scan(packagePrefix);
	}

	@SuppressWarnings("unchecked")
	private void scanEndpoints(String packagePrefix) {
		Reflections reflections = new Reflections(packagePrefix);
		Set<Class<?>> clazzes = reflections.getTypesAnnotatedWith(Endpoint.class);

		for (Class<?> endpoint : clazzes) {
			Endpoint annotation = endpoint.getAnnotation(Endpoint.class);
			endpoints.put(annotation.value(), (Class<? extends DatastoreObject>) endpoint);
		}
	}

	private void response(HttpServletResponse resp, HttpResponse httpResponse) throws IOException {
		httpResponse.execute(resp);
	}

	private void forbidden(HttpServletResponse resp) {
		resp.setStatus(403);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			HttpResponse responseJson = execute(req, req.getMethod(), getPath(req), JsonUtils.readJson(req.getReader()), makeParams(req));

			response(resp, responseJson);

		} catch (DatastoreException e) {
			forbidden(resp);
		}
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

	protected HttpResponse execute(HttpServletRequest req, String method, String path, String requestJson, Map<String, String> params) {
		DatastoreRouter router = new DatastoreRouter(method, path);
		Class<? extends DatastoreObject> clazz = endpoints.get(router.getEndpointPath());

		Repository r = getRepository(params);

		switch (router.getAction()) {
		case INDEX:
			return new JsonResponse(index(r, clazz, params == null ? null : params.get("q")));
		case SHOW:
			return new JsonResponse(get(r, clazz, router.getId()));
		case CREATE:
			return new JsonResponse(save(r, clazz, requestJson));
		case UPDATE:
			return new JsonResponse(save(r, clazz, requestJson));
		case CUSTOM:
			return action(r, clazz, router.getMethod(), router.getCustomAction(), router.getId(), params);
		}

		throw new IllegalArgumentException("Invalid datastore action");
	}

	protected Repository getRepository(Map<String, String> params) {
		return new Repository();
	}

	private HttpResponse action(Repository r, Class<? extends DatastoreObject> clazz, String method, String customAction, Long id,
			Map<String, String> params) {
		return r.action(clazz, method, customAction, id, params);
	}

	private String save(Repository r, Class<? extends DatastoreObject> clazz, String json) {
		logger.warning("JSON: " + json);

		if (JsonUtils.isJsonArray(json)) {
			return saveFromArray(r, clazz, json);
		} else {
			return saveFromObject(r, clazz, json);
		}
	}

	private String saveFromObject(Repository r, Class<? extends DatastoreObject> clazz, String json) {
		DatastoreObject object = JsonUtils.from(json, clazz);

		r.save(object);

		return JsonUtils.to(object);
	}

	private String saveFromArray(Repository r, Class<? extends DatastoreObject> clazz, String json) {
		List<? extends DatastoreObject> objects = JsonUtils.fromArray(json, clazz);

		for (DatastoreObject object : objects) {
			r.save(object);
		}

		return JsonUtils.to(objects);
	}

	private String index(Repository r, Class<? extends DatastoreObject> clazz, String q) {
		if (q == null) {
			return JsonUtils.to(r.all(clazz));
		}

		return JsonUtils.to(r.query(clazz).options(DatastoreQueryOptions.parse(q)).asList());
	}

	private String get(Repository r, Class<? extends DatastoreObject> clazz, long id) {
		return JsonUtils.to(r.findById(id, clazz));
	}

}
