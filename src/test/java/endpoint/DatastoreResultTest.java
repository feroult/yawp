package endpoint;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class DatastoreResultTest extends EndpointTestCase {

	@Test
	public void testFind() {
		SimpleObject object = new SimpleObject("xpto");
		r.save(object);

		DatastoreResult<SimpleObject> result = r.query(SimpleObject.class).id(object.getId());
		object = result.now();

		assertEquals("xpto", object.getaString());
	}

	@Test
	public void testQuery() {
		SimpleObject object = new SimpleObject("xpto");
		r.save(object);

		DatastoreResult<SimpleObject> result = r.query(SimpleObject.class).first();
		object = result.now();

		assertEquals("xpto", object.getaString());
	}

}
