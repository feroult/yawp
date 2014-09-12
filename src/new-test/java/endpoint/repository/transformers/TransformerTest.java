package endpoint.repository.transformers;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import endpoint.repository.models.basic.BasicObject;
import endpoint.utils.EndpointTestCase;

public class TransformerTest extends EndpointTestCase {

	@Test
	public void testSingleResult() {
		BasicObject object = new BasicObject("xpto");
		r.save(object);

		Map<String, Object> map = r.query(BasicObject.class).<Map<String, Object>> transform("simple").id(object.getId());

		assertEquals("xpto", map.get("innerValue"));
		assertEquals("xpto", ((BasicObject) map.get("innerObject")).getStringValue());
	}

	// @Test
	// public void testListResult() {
	// r.save(new SimpleObject("xpto1", 1l));
	// r.save(new SimpleObject("xpto1", 2l));
	// r.save(new SimpleObject("xpto2", 1l));
	// r.save(new SimpleObject("xpto2", 2l));
	// r.save(new SimpleObject("xpto3", 1l));
	// r.save(new SimpleObject("xpto3", 2l));
	//
	// List<Map<String, String>> list = r.query(SimpleObject.class).<Map<String,
	// String>> transform("simple").sort("innerValue", "desc")
	// .sort("innerObject", "aLong", "desc").list();
	//
	// assertEquals("xpto3", list.get(0).get("innerValue"));
	// assertEquals("xpto3", list.get(1).get("innerValue"));
	// assertEquals("xpto2", list.get(2).get("innerValue"));
	// assertEquals("xpto2", list.get(3).get("innerValue"));
	// assertEquals("xpto1", list.get(4).get("innerValue"));
	// assertEquals("xpto1", list.get(5).get("innerValue"));
	// }

}
