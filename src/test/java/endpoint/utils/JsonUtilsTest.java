package endpoint.utils;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.SimpleObject;
import endpoint.utils.JsonUtils;

public class JsonUtilsTest {

	private static final String SIMPLE_OBJECT_JSON = "{aInt : 1, aLong : 1, aDouble : 1.1, aBoolean : true, aDate : '2013/12/26 23:55:01', aString : object1}";

	@Test
	public void testFrom() {
		SimpleObject object = JsonUtils.from(SIMPLE_OBJECT_JSON, SimpleObject.class);
		object.assertObjectWithoutKey(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testFromArray() {
		String json = String.format("[%s, %s, %s]", SIMPLE_OBJECT_JSON, SIMPLE_OBJECT_JSON, SIMPLE_OBJECT_JSON);

		List<SimpleObject> objects = JsonUtils.fromArray(json, SimpleObject.class);

		assertEquals(3, objects.size());

		objects.get(0).assertObjectWithoutKey(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(0).assertObjectWithoutKey(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(0).assertObjectWithoutKey(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

}
