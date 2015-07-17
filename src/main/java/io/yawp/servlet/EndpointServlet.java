package io.yawp.servlet;

import io.yawp.commons.http.ErrorResponse;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.JsonResponse;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.EndpointException;
import io.yawp.repository.EndpointScanner;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;

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

	private static final long serialVersionUID = 8155293897299089610L;

	private RepositoryFeatures features;

	private boolean enableHooks = true;
	private boolean enableCrossDomain = false;

	public EndpointServlet() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		setWithHooks(config.getInitParameter("enableHooks"));
		setCrossDomain(config.getInitParameter("enableCrossDomain"));
		scanEndpoints(config.getInitParameter("packagePrefix"));
	}

	private void setWithHooks(String enableHooksParameter) {
		boolean enableHooks = enableHooksParameter == null || Boolean.valueOf(enableHooksParameter);
		setWithHooks(enableHooks);
	}

	private void setCrossDomain(String enableCrossDomainParameter) {
		if (enableCrossDomainParameter != null) {
			this.enableCrossDomain = Boolean.valueOf(enableCrossDomainParameter);
		}
	}

	protected void setWithHooks(boolean enableHooks) {
		this.enableHooks = enableHooks;
	}

	protected EndpointServlet(String packagePrefix) {
		scanEndpoints(packagePrefix);
	}

	private void scanEndpoints(String packagePrefix) {
		features = new EndpointScanner(packagePrefix).enableHooks(enableHooks).scan();
	}

	protected void response(HttpServletResponse resp, HttpResponse httpResponse) throws IOException {
		if (httpResponse == null) {
			new JsonResponse("{\"status\":\"ok\"}").execute(resp);
		} else {
			httpResponse.execute(resp);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpResponse httpResponse;
		try {
			httpResponse = execute(req.getMethod(), getUri(req), JsonUtils.readJson(req.getReader()), makeParams(req));
		} catch (HttpException e) {
			httpResponse = new ErrorResponse(e.getHttpStatus(), e.getText());
		} catch (EndpointException e) {
			httpResponse = new ErrorResponse(403);
		}

		if (enableCrossDomain) {
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
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

	public HttpResponse execute(String method, String uri, String requestJson, Map<String, String> params) {
		Repository r = getRepository(params);
		EndpointRouter router = EndpointRouter.parse(r, HttpVerb.fromString(method), uri, requestJson, params);
		return router.executeRestAction(enableHooks);
	}

	protected Repository getRepository(Map<String, String> params) {
		return Repository.r().setFeatures(features);
	}
}
