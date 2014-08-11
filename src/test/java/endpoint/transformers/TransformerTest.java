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
		Map<String, String> map = r.query(SimpleObject.class).transform(Map.class, "simple").returnById(object.getId());

		assertEquals("xpto", map.get("innerValue"));
	}

	@Test
	public void testListResult() {
		r.save(new SimpleObject("xpto1", 1l));
		r.save(new SimpleObject("xpto1", 2l));
		r.save(new SimpleObject("xpto2", 1l));
		r.save(new SimpleObject("xpto2", 2l));
		r.save(new SimpleObject("xpto3", 1l));
		r.save(new SimpleObject("xpto3", 2l));

		@SuppressWarnings("rawtypes")
		List<Map> list = r.query(SimpleObject.class).transform(Map.class, "simple").sort("innerValue", "desc")
				.sort("innerObject", "aLong", "desc").list();

		assertEquals("xpto3", list.get(0).get("innerValue"));
		assertEquals("xpto3", list.get(1).get("innerValue"));
		assertEquals("xpto2", list.get(2).get("innerValue"));
		assertEquals("xpto2", list.get(3).get("innerValue"));
		assertEquals("xpto1", list.get(4).get("innerValue"));
		assertEquals("xpto1", list.get(5).get("innerValue"));
	}

}
