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
		Map<Long, String> map = (Map<Long, String>) r.query(SimpleObject.class).id(object.getId()).transform("simple").now();

		assertEquals("xpto", map.get(object.getId()));
	}

}
