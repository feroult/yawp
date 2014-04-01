package endpoint;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import endpoint.response.HttpResponse;
import endpoint.response.JsonResponse;
import endpoint.utils.JsonUtils;

public class DatastoreServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(DatastoreServlet.class.getSimpleName());

	private static final long serialVersionUID = 8155293897299089610L;

	protected Class<? extends DatastoreObject> clazz;

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String servletName = config.getServletName();

		try {
			clazz = (Class<? extends DatastoreObject>) Class.forName(servletName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public DatastoreServlet() {
	}

	public DatastoreServlet(Class<? extends DatastoreObject> clazz) {
		this.clazz = clazz;
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
			HttpResponse responseJson = execute(req.getMethod(), req.getRequestURI(), JsonUtils.readJson(req.getReader()),
					req.getParameter("q"));

			response(resp, responseJson);

		} catch (DatastoreException e) {
			forbidden(resp);
		}
	}

	protected HttpResponse execute(String method, String path, String requestJson, String q) {
		DatastoreRouter router = new DatastoreRouter(method, path);

		switch (router.getAction()) {
		case INDEX:
			return new JsonResponse(index(q));
		case SHOW:
			return new JsonResponse(get(router.getId()));
		case CREATE:
			return new JsonResponse(save(requestJson));
		case UPDATE:
			return new JsonResponse(save(requestJson));
		case CUSTOM:
			return action(router.getMethod(), router.getCustomAction(), router.getId());
		}

		throw new IllegalArgumentException("Invalid datastore action");
	}

	private HttpResponse action(String method, String customAction, Long id) {
		Repository r = new Repository();
		return r.action(clazz, method, customAction, id);
	}

	private String save(String json) {
		logger.warning("JSON: " + json);

		if (JsonUtils.isJsonArray(json)) {
			return saveFromArray(json);
		} else {
			return saveFromObject(json);
		}
	}

	private String saveFromObject(String json) {
		DatastoreObject object = JsonUtils.from(json, clazz);

		Repository r = new Repository();
		r.save(object);

		return JsonUtils.to(object);
	}

	private String saveFromArray(String json) {
		Repository r = new Repository();

		List<? extends DatastoreObject> objects = JsonUtils.fromArray(json, clazz);

		for (DatastoreObject object : objects) {
			r.save(object);
		}

		return JsonUtils.to(objects);
	}

	private String index(String q) {
		Repository r = new Repository();

		if (q == null) {
			return JsonUtils.to(r.all(clazz));
		}

		return JsonUtils.to(r.query(clazz).options(DatastoreQueryOptions.parse(q)).asList());
	}

	private String get(long id) {
		Repository r = new Repository();
		return JsonUtils.to(r.findById(id, clazz));
	}

}
