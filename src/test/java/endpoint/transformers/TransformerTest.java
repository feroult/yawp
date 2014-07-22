package endpoint.transformers;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import endpoint.SimpleObject;
import endpoint.utils.EndpointTestCase;

public class TransformerTest extends EndpointTestCase {

	@Test
	public void testSingleResult() {
		SimpleObject object = new SimpleObject("xpto");
		r.save(object);

		@SuppressWarnings("unchecked")
		Map<String, String> map = r.query(SimpleObject.class).transform(Map.class, "simple").id(object.getId());

		assertEquals("xpto", map.get("innerObject"));
	}

	@Test
	public void testListResult() {
		SimpleObject object1 = new SimpleObject("xpto1");
		SimpleObject object2 = new SimpleObject("xpto2");
		SimpleObject object3 = new SimpleObject("xpto3");
		r.save(object1);
		r.save(object2);
		r.save(object3);

		@SuppressWarnings("rawtypes")
		List<Map> list = r.query(SimpleObject.class).transform(Map.class, "simple").order("aString", "asc").list();

		assertEquals("xpto1", list.get(0).get("innerObject"));
		assertEquals("xpto2", list.get(1).get("innerObject"));
		assertEquals("xpto3", list.get(2).get("innerObject"));
	}

	// TODO remove it ?!
	// @Test
	// @Ignore
	// @SuppressWarnings("rawtypes")
	// public void testListResultWithInnerOrder() {
	// SimpleObject object1 = new SimpleObject("xpto1");
	// SimpleObject object2 = new SimpleObject("xpto2");
	// SimpleObject object3 = new SimpleObject("xpto3");
	// r.save(object1);
	// r.save(object2);
	// r.save(object3);
	//
	// DatastoreQueryTransformer<Map> transform =
	// r.query(SimpleObject.class).transform(Map.class, "simple");
	//
	// //List<Map> list = transform.order("aString", "asc", "innerObject",
	// array("value", "desc")).list();
	// //List<Map> list = transform.order("aString", "asc").list();
	// List<Map> list = transform.order("aString", "asc").order("innerObject",
	// "name", "asc").list();
	//
	// assertEquals("xpto1", list.get(0).get("xpto1"));
	// }

//	private String[] array(String value, String direction) {
//		return new String[] { value, direction };
//	}

}
