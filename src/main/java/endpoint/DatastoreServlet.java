package endpoint;

import java.io.IOException;
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

import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;

public class DatastoreServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(DatastoreServlet.class.getSimpleName());

	private static final long serialVersionUID = 8155293897299089610L;

	protected String packagePrefix;

	private Map<String, Class<? extends DatastoreObject>> endpoints = new HashMap<String, Class<? extends DatastoreObject>>();;

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String servletName = config.getServletName();

		// try {
		// // clazzX = (Class<? extends DatastoreObject>)
		// Class.forName(servletName);
		// } catch (ClassNotFoundException e) {
		// throw new RuntimeException(e);
		// }
	}

	public DatastoreServlet() {
	}

	public DatastoreServlet(Class<? extends DatastoreObject> clazz) {
		// this.clazzX = clazz;
	}

	protected DatastoreServlet(String packagePrefix) {
		this.packagePrefix = packagePrefix;
		scanEndpoints();
	}

	@SuppressWarnings("unchecked")
	private void scanEndpoints() {
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
			HttpResponse responseJson = execute(req.getMethod(), getEndpointPath(req), JsonUtils.readJson(req.getReader()),
					req.getParameter("q"));

			response(resp, responseJson);

		} catch (DatastoreException e) {
			forbidden(resp);
		}
	}

	private String getEndpointPath(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getServletPath().length());
	}

	protected HttpResponse execute(String method, String path, String requestJson, String q) {
		DatastoreRouter router = new DatastoreRouter(method, path);

		Class<? extends DatastoreObject> clazz = endpoints.get(router.getEndpointPath());

		switch (router.getAction()) {
		case INDEX:
			return new JsonResponse(index(clazz, q));
		case SHOW:
			return new JsonResponse(get(clazz, router.getId()));
		case CREATE:
			return new JsonResponse(save(clazz, requestJson));
		case UPDATE:
			return new JsonResponse(save(clazz, requestJson));
		case CUSTOM:
			return action(clazz, router.getMethod(), router.getCustomAction(), router.getId());
		}

		throw new IllegalArgumentException("Invalid datastore action");
	}

	private HttpResponse action(Class<? extends DatastoreObject> clazz, String method, String customAction, Long id) {
		Repository r = new Repository();
		return r.action(clazz, method, customAction, id);
	}

	private String save(Class<? extends DatastoreObject> clazz, String json) {
		logger.warning("JSON: " + json);

		if (JsonUtils.isJsonArray(json)) {
			return saveFromArray(clazz, json);
		} else {
			return saveFromObject(clazz, json);
		}
	}

	private String saveFromObject(Class<? extends DatastoreObject> clazz, String json) {
		DatastoreObject object = JsonUtils.from(json, clazz);

		Repository r = new Repository();
		r.save(object);

		return JsonUtils.to(object);
	}

	private String saveFromArray(Class<? extends DatastoreObject> clazz, String json) {
		Repository r = new Repository();

		List<? extends DatastoreObject> objects = JsonUtils.fromArray(json, clazz);

		for (DatastoreObject object : objects) {
			r.save(object);
		}

		return JsonUtils.to(objects);
	}

	private String index(Class<? extends DatastoreObject> clazz, String q) {
		Repository r = new Repository();

		if (q == null) {
			return JsonUtils.to(r.all(clazz));
		}

		return JsonUtils.to(r.query(clazz).options(DatastoreQueryOptions.parse(q)).asList());
	}

	private String get(Class<? extends DatastoreObject> clazz, long id) {
		Repository r = new Repository();
		return JsonUtils.to(r.findById(id, clazz));
	}

}
