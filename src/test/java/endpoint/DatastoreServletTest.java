package endpoint;

import endpoint.response.ErrorResponse;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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
		String json = servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText();

		SimpleObject object = JsonUtils.from(json, SimpleObject.class);

		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");

		assertEquals(1, object.getaList().size());
		object.getaList().get(0).assertAnotherObject("anotherObject1");
	}

	@Test
	public void testCreateArray() {
		String json = servlet.execute("POST", "/simpleobjects", SIMPLE_ARRAY_JSON, null).getText();

		List<SimpleObject> objects = JsonUtils.fromList(json, SimpleObject.class);

		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");

		assertEquals(1, objects.get(0).getaList().size());
		objects.get(0).getaList().get(0).assertAnotherObject("anotherObject1");
	}

	@Test
	public void testIndex() {
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);

		String json = servlet.execute("GET", "/simpleobjects", null, null).getText();

		List<SimpleObject> objects = JsonUtils.fromList(json, SimpleObject.class);

		assertEquals(2, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testCustomAction() {
		String json = servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText();

		SimpleObject object = JsonUtils.from(json, SimpleObject.class);

		json = servlet.execute("PUT", "/simpleobjects/" + object.getId() + "/active", null, null).getText();

		object = JsonUtils.from(json, SimpleObject.class);
		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "i was changed in action");
	}

	@Test
	public void testTransformerInShow() {
		SimpleObject object = JsonUtils.from(servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText(),
				SimpleObject.class);

		String json = servlet.execute("GET", "/simpleobjects/" + object.getId(), null, t("simple")).getText();

		@SuppressWarnings("rawtypes")
		Map map = JsonUtils.from(json, Map.class);

		assertEquals("object1", map.get("innerValue"));
	}

	@Test
	public void testTransformerInIndex() {
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);
		servlet.execute("POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);

		String json = servlet.execute("GET", "/simpleobjects", null, t("simple")).getText();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Map> list = JsonUtils.from(json, List.class);

		assertEquals("object1", list.get(0).get("innerValue"));
		assertEquals("object1", list.get(1).get("innerValue"));
	}

	@Test
	public void testEndpointRestrictions() {
		assertFalse(ErrorResponse.class.isInstance(servlet.execute("GET", "/simpleobjects", SIMPLE_OBJECT_JSON, null)));
		assertTrue(ErrorResponse.class.isInstance(servlet.execute("GET", "/anothersimpleobjects", SIMPLE_OBJECT_JSON, null)));
	}

    private Map<String, String> t(String s) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("t", s);
		return map;
	}
}
