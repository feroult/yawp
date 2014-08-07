package endpoint;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class IdRefTest extends EndpointTestCase {

	@Test
	public void testSave() {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);

		object = object.getId().fetch();

		assertEquals("xpto", object.getText());
	}
}
