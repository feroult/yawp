package endpoint;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class DatastoreResultTest extends EndpointTestCase {

	@Test
	public void testFind() {
		SimpleObject object = new SimpleObject("xpto");
		r.save(object);

		DatastoreResult<SimpleObject> result = r.find(SimpleObject.class, object.getId());
		object = result.now();
		
		assertEquals("xpto", object.getaString());
	}

}
