package endpoint;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import endpoint.utils.EndpointTestCase;

public class IdRefTest extends EndpointTestCase {

	@Test
	public void testSave() throws HttpException {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);

		object = object.getId().fetch();

		assertEquals("xpto", object.getText());
	}

	@Test
	public void testWithRelation() throws HttpException {
		AnotherSimpleObject anotherObject = new AnotherSimpleObject("haha");
		r.save(anotherObject);

		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		object.setAnotherSimpleObjectId(IdRef.create(r, AnotherSimpleObject.class, anotherObject.getId()));

		r.save(object);

		object = object.getId().fetch();
		anotherObject = object.getAnotherSimpleObjectId().fetch();

		assertEquals("haha", anotherObject.getaString());
	}
}
