package io.yawp.servlet;

import io.yawp.repository.EndpointException;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.response.ErrorResponse;
import io.yawp.repository.response.HttpResponse;
import io.yawp.repository.response.JsonResponse;
import io.yawp.utils.Environment;
import io.yawp.utils.HttpVerb;
import io.yawp.utils.JsonUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EndpointServlet extends HttpServlet {

	private static final String TASKS_PREFIX = "/_tasks";

	private static final long serialVersionUID = 8155293897299089610L;

	private RepositoryFeatures features;

	private boolean enableHooks = true;

	private boolean enableProduction = false;

	public EndpointServlet() {
	}

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
		EndpointRouter router = EndpointRouter.parse(r, HttpVerb.fromString(method), normalizeUri(uri));
		return router.executeRestAction(enableHooks, requestJson, params);
	}

	private String normalizeUri(String uri) {
		if (uri.startsWith(TASKS_PREFIX)) {
			return uri.substring(TASKS_PREFIX.length());
		}
		return uri;
	}

	protected Repository getRepository(Map<String, String> params) {
		return Repository.r().setFeatures(features);
	}
}
