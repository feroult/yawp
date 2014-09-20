package io.yawp.repository.transformers;

import static org.junit.Assert.assertEquals;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;
import io.yawp.utils.EndpointTestCase;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TransformerTest extends EndpointTestCase {

	@Test
	public void testSingleResult() {
		BasicObject object = new BasicObject("xpto");
		r.save(object);

		Map<String, Object> map = r.query(BasicObject.class).<Map<String, Object>> transform("simple").id(object.getId());

		assertEquals("xpto", map.get("innerValue"));
		assertEquals("xpto", ((BasicObject) map.get("innerObject")).getStringValue());
	}

	@Test
	public void testListResultWithSort() {
		r.save(new BasicObject("xpto1"));
		r.save(new BasicObject("xpto2"));

		List<Map<String, Object>> list = r.query(BasicObject.class).<Map<String, Object>> transform("simple").sort("innerValue", "desc")
				.list();

		assertEquals("xpto2", list.get(0).get("innerValue"));
		assertEquals("xpto1", list.get(1).get("innerValue"));
	}

	@Test
	public void testTransformWithChild() {
		Parent parent = new Parent();
		r.save(parent);

		Child child = new Child();
		child.setName("xpto");
		child.setParentId(parent.getId());
		r.save(child);

		Child retrievedChild = r.query(Child.class).<Child> transform("simple").first();
		assertEquals("transformed xpto", retrievedChild.getName());
	}

}
