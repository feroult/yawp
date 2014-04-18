package endpoint.transformers;

import static org.junit.Assert.assertEquals;

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
		Map<Long, String> map = r.query(SimpleObject.class).id(object.getId()).transform(Map.class, "simple").now();

		assertEquals("xpto", map.get(object.getId()));
	}

}
