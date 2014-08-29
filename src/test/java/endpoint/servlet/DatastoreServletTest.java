package endpoint.servlet;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.SimpleObject;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class DatastoreServletTest extends EndpointTestCase {

	private static final String SIMPLE_OBJECT_JSON = "{aInt : 1, aLong : 1, aDouble : 1.1, aBoolean : true, aDate : '2013/12/26 23:55:01', aString : object1, aList : [{aString : anotherObject1}]}";
	private static final String SIMPLE_ARRAY_JSON = String.format("[%s, %s]", SIMPLE_OBJECT_JSON, SIMPLE_OBJECT_JSON);

	private DatastoreServlet servlet;

	@Before
	public void before() {
		servlet = new DatastoreServlet("endpoint");
	}

	@Test
	public void testCreate() {
		String json;
		json = servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText();

		SimpleObject object = JsonUtils.from(r, json, SimpleObject.class);

		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testCreateArray() {
		String json;
		json = servlet.execute("POST", "/simpleobjects", SIMPLE_ARRAY_JSON, null).getText();

		List<SimpleObject> objects = JsonUtils.fromList(r, json, SimpleObject.class);

		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testIndex() {
		String json;
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);

		json = servlet.execute("GET", "/simpleobjects", null, null).getText();

		List<SimpleObject> objects = JsonUtils.fromList(r, json, SimpleObject.class);

		assertEquals(2, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testCustomAction() {
		String json;
		SimpleObject object;
		json = servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText();

		object = JsonUtils.from(r, json, SimpleObject.class);

		json = servlet.execute("PUT", "/simpleobjects/" + object.getId() + "/active", null, null).getText();

		object = JsonUtils.from(r, json, SimpleObject.class);
		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "i was changed in action");
	}

	@Test
	public void testTransformerInShow() {
		String json;
		SimpleObject object = JsonUtils.from(r, servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText(),
				SimpleObject.class);

		json = servlet.execute("GET", "/simpleobjects/" + object.getId(), null, t("simple")).getText();

		@SuppressWarnings("rawtypes")
		Map map = JsonUtils.from(r, json, Map.class);

		assertEquals("object1", map.get("innerValue"));
	}

	@Test
	public void testTransformerInIndex() {
		String json;
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);

		json = servlet.execute("GET", "/simpleobjects", null, t("simple")).getText();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Map> list = JsonUtils.from(r, json, List.class);

		assertEquals("object1", list.get(0).get("innerValue"));
		assertEquals("object1", list.get(1).get("innerValue"));
	}

	@Test
	public void testEndpointRestrictions() {
		testServletExecute(200, "GET", "/simpleobjects", SIMPLE_OBJECT_JSON, null);
		testServletExecute(403, "GET", "/anothersimpleobjects", SIMPLE_OBJECT_JSON, null);
	}

	private void testServletExecute(int httpStatus, String method, String path, String simpleObjectJson, Map<String, String> params) {
		try {
			servlet.execute(method, path, simpleObjectJson, params);
			assertEquals(httpStatus, 200);
		} catch (HttpException e) {
			assertEquals(httpStatus, e.getHttpStatus());
		}
	}

	private Map<String, String> t(String s) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("t", s);
		return map;
	}
}
