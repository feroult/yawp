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
		Map<Long, String> map = r.query(SimpleObject.class).transform(Map.class, "simple").id(object.getId());

		assertEquals("xpto", map.get(object.getId()));
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

		assertEquals("xpto1", list.get(0).get(object1.getId()));
		assertEquals("xpto2", list.get(1).get(object2.getId()));
		assertEquals("xpto3", list.get(2).get(object3.getId()));
	}

}
