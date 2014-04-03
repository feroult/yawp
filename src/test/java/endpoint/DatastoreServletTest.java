package endpoint;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.utils.GAETest;
import endpoint.utils.JsonUtils;

public class DatastoreServletTest extends GAETest {

	private static final String SIMPLE_OBJECT_JSON = "{aInt : 1, aLong : 1, aDouble : 1.1, aBoolean : true, aDate : '2013/12/26 23:55:01', aString : object1, aList : [{aString : anotherObject1}]}";
	private static final String SIMPLE_ARRAY_JSON = String.format("[%s, %s]", SIMPLE_OBJECT_JSON, SIMPLE_OBJECT_JSON);

	private DatastoreServlet servlet;

	@Before
	public void before() {
		servlet = new DatastoreServlet("endpoint");
	}

	@Test
	public void testCreate() {
		String json = servlet.execute(null, "POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText();

		SimpleObject object = JsonUtils.from(json, SimpleObject.class);

		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");

		assertEquals(1, object.getaList().size());
		object.getaList().get(0).assertAnotherObject("anotherObject1");
	}

	@Test
	public void testCreateArray() {
		String json = servlet.execute(null, "POST", "/simpleobjects", SIMPLE_ARRAY_JSON, null).getText();

		List<SimpleObject> objects = JsonUtils.fromArray(json, SimpleObject.class);

		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");

		assertEquals(1, objects.get(0).getaList().size());
		objects.get(0).getaList().get(0).assertAnotherObject("anotherObject1");
	}

	@Test
	public void testIndex() {
		servlet.execute(null, "POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);
		servlet.execute(null, "POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null);

		String json = servlet.execute(null, "GET", "/simpleobjects", null, null).getText();

		List<SimpleObject> objects = JsonUtils.fromArray(json, SimpleObject.class);

		assertEquals(2, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testCustomAction() {
		String json = servlet.execute(null, "POST", "/simpleobjects", SIMPLE_OBJECT_JSON, null).getText();

		SimpleObject object = JsonUtils.from(json, SimpleObject.class);

		json = servlet.execute(null, "PUT", "/simpleobjects/" + object.getId() + "/active", null, null).getText();

		object = JsonUtils.from(json, SimpleObject.class);
		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "i was changed in action");
	}
}
