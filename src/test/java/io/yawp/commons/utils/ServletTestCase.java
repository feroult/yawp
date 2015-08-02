package io.yawp.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.Repository;
import io.yawp.servlet.EndpointServlet;
import io.yawp.servlet.HttpException;

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
				return yawp;
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
		return post(uri, json, new HashMap<String, String>());
	}

	protected String post(String uri, String json, Map<String, String> params) {
		return servlet.execute("POST", uri, json, params).getText();
	}

	protected void assertPostWithStatus(String uri, String json, int status) {
		try {
			post(uri, json);
		} catch (HttpException e) {
			assertEquals(status, e.getHttpStatus());
			return;
		}
		assertTrue(status == 200);
	}

	protected String put(String uri) {
		return put(uri, null, new HashMap<String, String>());
	}

	protected String put(String uri, String json) {
		return put(uri, json, new HashMap<String, String>());
	}

	protected String patch(String uri) {
		return patch(uri, null, new HashMap<String, String>());
	}

	protected String patch(String uri, String json) {
		return patch(uri, json, new HashMap<String, String>());
	}

	protected void assertPutWithStatus(String uri, String json, Map<String, String> params, int status) {
		try {
			put(uri, json, params);
		} catch (HttpException e) {
			assertEquals(status, e.getHttpStatus());
			return;
		}
		assertTrue(status == 200);
	}

	protected void assertPutWithStatus(String uri, int status) {
		assertPutWithStatus(uri, null, new HashMap<String, String>(), status);
	}

	protected void assertPutWithStatus(String uri, String json, int status) {
		assertPutWithStatus(uri, json, new HashMap<String, String>(), status);
	}

	protected void assertPutWithStatus(String uri, Map<String, String> params, int status) {
		assertPutWithStatus(uri, null, params, status);
	}

	protected String put(String uri, Map<String, String> params) {
		return servlet.execute("PUT", uri, null, params).getText();
	}

	protected String put(String uri, String json, Map<String, String> params) {
		return servlet.execute("PUT", uri, json, params).getText();
	}

	protected String patch(String uri, Map<String, String> params) {
		return servlet.execute("PATCH", uri, null, params).getText();
	}

	protected String patch(String uri, String json, Map<String, String> params) {
		return servlet.execute("PATCH", uri, json, params).getText();
	}

	protected String delete(String uri) {
		return servlet.execute("DELETE", uri, null, new HashMap<String, String>()).getText();
	}

	protected void assertDeleteWithStatus(String uri, int status) {
		try {
			delete(uri);
		} catch (HttpException e) {
			assertEquals(status, e.getHttpStatus());
			return;
		}
		assertTrue(status == 200);
	}

	protected <T> T from(String json, Class<T> clazz) {
		return JsonUtils.from(yawp, json, clazz);
	}

	protected <T> List<T> fromList(String json, Class<T> clazz) {
		return JsonUtils.fromList(yawp, json, clazz);
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
