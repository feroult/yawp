package endpoint;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import endpoint.utils.EndpointTestCase;
import endpoint.utils.JsonUtils;

public class IdRefTest extends EndpointTestCase {

	@Test
	public void testSave() {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);

		object = object.getId().fetch();

		assertEquals("xpto", object.getText());
	}

	@Test
	public void testWithRelation() {
		ObjectWithIdRef object = saveObjectWithRelation();

		object = object.getId().fetch();
		AnotherSimpleObject anotherObject = object.getAnotherSimpleObjectId().fetch();

		assertEquals("haha", anotherObject.getaString());
	}

	@Test
	public void testQuery() {
		ObjectWithIdRef object = saveObjectWithRelation();

		object = r.query(ObjectWithIdRef.class).where("id", "=", object.getId().asLong()).only();
		assertEquals("xpto", object.getText());

		object = r.query(ObjectWithIdRef.class).where("anotherSimpleObjectId", "=", object.getAnotherSimpleObjectId().asLong()).only();
		assertEquals("xpto", object.getText());

		object = r.query(ObjectWithIdRef.class).where("anotherSimpleObjectId", "=", object.getAnotherSimpleObjectId()).only();
		assertEquals("xpto", object.getText());

	}

	@Test
	public void testJsonConversion() {
		ObjectWithIdRef object = saveObjectWithRelation();

		String json = JsonUtils.to(object);
		object = JsonUtils.from(r, json, ObjectWithIdRef.class);

		AnotherSimpleObject anotherObject = object.getAnotherSimpleObjectId().fetch();
		assertEquals("haha", anotherObject.getaString());
	}

	@Test
	public void testWithJsonListField() {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);

		AnotherSimpleObject anotherObject1 = new AnotherSimpleObject("hehe");
		r.save(anotherObject1);

		AnotherSimpleObject anotherObject2 = new AnotherSimpleObject("hihi");
		r.save(anotherObject2);

		String json = String.format("{id: %d, text: 'lala', objectIds: [%d, %d]}", object.getId().asLong(), anotherObject1.getId(),
				anotherObject2.getId());

		object = JsonUtils.from(r, json, ObjectWithIdRef.class);

		assertEquals("lala", object.getText());
		assertEquals("hehe", object.getObjectIds().get(0).fetch().getaString());
		assertEquals("hihi", object.getObjectIds().get(1).fetch().getaString());
	}

	private ObjectWithIdRef saveObjectWithRelation() {
		AnotherSimpleObject anotherObject = new AnotherSimpleObject("haha");
		r.save(anotherObject);

		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		object.setAnotherSimpleObjectId(IdRef.create(r, AnotherSimpleObject.class, anotherObject.getId()));

		r.save(object);

		return object;
	}
}
