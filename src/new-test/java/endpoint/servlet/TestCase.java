package endpoint.servlet;

import java.util.List;
import java.util.Map;

import org.junit.Before;

import endpoint.repository.Repository;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class TestCase extends EndpointTestCase {

	private EndpointServlet servlet;

	@Before
	public void before() {
		servlet = new EndpointServlet("endpoint") {

			private static final long serialVersionUID = 3374113392343671861L;

			@Override
			protected Repository getRepository(Map<String, String> params) {
				return r;
			}

		};
	}

	protected String get(String uri) {
		return get(uri, null);
	}

	protected String get(String uri, Map<String, String> params) {
		return servlet.execute("GET", uri, null, null).getText();
	}

	protected String post(String uri, String json) {
		return post(uri, json, null);
	}

	protected String post(String uri, String json, Map<String, String> params) {
		return servlet.execute("POST", uri, json, null).getText();
	}

	protected String delete(String uri) {
		return servlet.execute("DELETE", uri, null, null).getText();
	}

	protected <T> T from(String json, Class<T> clazz) {
		return JsonUtils.from(r, json, clazz);
	}

	protected <T> List<T> fromList(String json, Class<T> clazz) {
		return JsonUtils.fromList(r, json, clazz);
	}
}
