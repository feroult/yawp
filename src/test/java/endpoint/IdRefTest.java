package endpoint;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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

	@Test
	public void testObjectWithChild() {
		ObjectWithIdRef object = new ObjectWithIdRef("xpto");
		r.save(object);

		ChildWithIdRef child = new ChildWithIdRef("child xpto");
		child.setObjectWithIdRefId(object.getId());
		r.save(child);

		ChildWithIdRef retrievedChild = object.getId().fetch(ChildWithIdRef.class);
		assertEquals("child xpto", retrievedChild.getText());
	}

	@Test
	public void testInOperator() {
		ObjectWithIdRef object1 = new ObjectWithIdRef("xpto1");
		r.save(object1);

		ObjectWithIdRef object2 = new ObjectWithIdRef("xpto2");
		r.save(object2);

		ObjectWithIdRef object3 = new ObjectWithIdRef("xpto3");
		r.save(object3);

		List<ObjectWithIdRef> objects = r.query(ObjectWithIdRef.class).where("id", "in", Arrays.asList(object1.getId().asLong(), object2.getId().asLong())).list();
		assertEquals(2, objects.size());
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
