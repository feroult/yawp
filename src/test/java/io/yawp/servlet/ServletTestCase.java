package io.yawp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.yawp.repository.Repository;
import io.yawp.utils.EndpointTestCase;
import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;

public class ServletTestCase extends EndpointTestCase {

	private EndpointServlet servlet;

	@Before
	public void createServlet() {
		servlet = new EndpointServlet("io.yawp") {

			private static final long serialVersionUID = 3374113392343671861L;

			@Override
			protected Repository getRepository(Map<String, String> params) {
				return r;
			}

		};
	}

	protected String get(String uri) {
		return get(uri, new HashMap<String, String>());
	}

	protected String get(String uri, Map<String, String> params) {
		return servlet.execute("GET", uri, null, params).getText();
	}

	protected void assertGetWithStatus(String uri, int status) {
		try {
			get(uri);
		} catch (HttpException e) {
			assertEquals(status, e.getHttpStatus());
			return;
		}
		assertTrue(status == 200);
	}

	protected String post(String uri, String json) {
		return post(uri, json, null);
	}

	protected String post(String uri, String json, Map<String, String> params) {
		return servlet.execute("POST", uri, json, params).getText();
	}

	protected String put(String uri) {
		return put(uri, null, null);
	}

	protected String put(String uri, String json) {
		return put(uri, json, null);
	}

	protected String put(String uri, Map<String, String> params) {
		return servlet.execute("PUT", uri, null, params).getText();
	}

	protected String put(String uri, String json, Map<String, String> params) {
		return servlet.execute("PUT", uri, json, params).getText();
	}

	protected String delete(String uri) {
		return servlet.execute("DELETE", uri, null, new HashMap<String, String>()).getText();
	}

	protected <T> T from(String json, Class<T> clazz) {
		return JsonUtils.from(r, json, clazz);
	}

	protected <T> List<T> fromList(String json, Class<T> clazz) {
		return JsonUtils.fromList(r, json, clazz);
	}

	protected String parseIds(String format, Object... objects) {
		List<String> longIds = new ArrayList<String>();

		for (Object object : objects) {
			longIds.add(String.valueOf(EntityUtils.getIdSimpleValue(object)));
		}

		return String.format(format, longIds.toArray());
	}

	protected String uri(String uriFormat, Object... objects) {
		return parseIds(uriFormat, objects);
	}

	protected String json(String uriFormat, Object... objects) {
		return parseIds(uriFormat, objects);
	}

	protected Map<String, String> params(String key, String value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		return map;
	}

}
