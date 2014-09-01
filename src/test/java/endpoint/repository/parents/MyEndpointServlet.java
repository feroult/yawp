package endpoint.repository.parents;

import java.util.Map;

import endpoint.repository.Repository;
import endpoint.repository.response.HttpResponse;
import endpoint.servlet.EndpointServlet;

class MyEndpointServlet extends EndpointServlet {
	private static final long serialVersionUID = 2770293412557653563L;

	public MyEndpointServlet(String packagePrefix) {
		super(packagePrefix);
	}

	public HttpResponse execute(String method, String path, String requestJson, Map<String, String> params) {
		return super.execute(method, path, requestJson, params);
	}

	public Repository r() {
		return super.getRepository(null);
	}
}