package endpoint.transformers;

import org.junit.Test;

import endpoint.SimpleObject;
import endpoint.utils.EndpointTestCase;

public class TransformerTest extends EndpointTestCase {

	@Test
	public void testSimpleTransformer() {
		SimpleObject object = new SimpleObject("xpto");
		r.save(object);

		Object o = r.query(SimpleObject.class).id(object.getId()).transform("simple").now();

		o.toString();
		//
		// r.query(SimpleObject.class).first().transform("simple").now();

	}

}
