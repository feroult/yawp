package endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;

public class RepositoryTest extends EndpointTestCase {

	@Test
	public void testSave() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");

		r.save(object);
		object = r.find(SimpleObject.class, object.getKey());

		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testSaveWithList() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		object.setaList(Arrays.asList(new AnotherSimpleObject("anotherObject1")));

		r.save(object);
		object = r.find(SimpleObject.class, object.getKey());

		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		assertEquals(1, object.getaList().size());
		assertEquals("anotherObject1", object.getaList().get(0).getaString());
	}

	@Test
	public void testSaveTwoObjectsWithList() {
		SimpleObject object1 = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		object1.setaList(Arrays.asList(new AnotherSimpleObject("anotherObject1")));

		SimpleObject object2 = new SimpleObject(2, 2l, 2.2, true, DateUtils.toTimestamp("2013/12/29 00:43:01"), "object2");
		object2.setaList(Arrays.asList(new AnotherSimpleObject("anotherObject2")));

		r.save(object1);
		r.save(object2);

		object1 = r.find(SimpleObject.class, object1.getKey());
		object2 = r.find(SimpleObject.class, object2.getKey());

		object1.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		assertEquals(1, object1.getaList().size());
		object1.getaList().get(0).assertAnotherObject("anotherObject1");

		object2.assertObject(2, 2l, 2.2, true, "2013/12/29 00:43:01", "object2");
		assertEquals(1, object2.getaList().size());
		object2.getaList().get(0).assertAnotherObject("anotherObject2");
	}

	@Test
	public void testFindById() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");

		r.save(object);

		object = r.find(SimpleObject.class, 1l).now();
		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");

	}

	@Test
	public void testDontDuplicateChildList() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		object.setaList(Arrays.asList(new AnotherSimpleObject("anotherObject1")));
		r.save(object);

		object = r.find(SimpleObject.class, object.getKey());
		object.setaList(Arrays.asList(new AnotherSimpleObject("anotherObject2")));
		r.save(object);

		object = r.find(SimpleObject.class, object.getKey());

		assertEquals(1, object.getaList().size());
		assertEquals("anotherObject2", object.getaList().get(0).getaString());
	}

	@Test
	public void testJsonProperty() {
		SimpleObject object = new SimpleObject();

		object.setaString("xpto");
		object.setNotADatastoreObject(new NotADatastoreObject("xpto"));

		r.save(object);

		object = r.find(SimpleObject.class, object.getId()).now();

		assertEquals("xpto", object.getaString());
		assertEquals("xpto", object.getNotADatastoreObject().getName());
	}

	@Test
	public void testJsonArrayProperty() {
		SimpleObject object = new SimpleObject();

		object.setaString("xpto");

		List<NotADatastoreObject> list = new ArrayList<NotADatastoreObject>();

		list.add(new NotADatastoreObject("xpto1"));
		list.add(new NotADatastoreObject("xpto2"));

		object.setNotADatastoreObjectList(list);

		r.save(object);

		object = r.find(SimpleObject.class, object.getId()).now();

		assertEquals("xpto", object.getaString());
		assertEquals("xpto1", object.getNotADatastoreObjectList().get(0).getName());
		assertEquals("xpto2", object.getNotADatastoreObjectList().get(1).getName());
	}

	@Test
	public void testDelete() {
		SimpleObject object = new SimpleObject("xpto");

		List<AnotherSimpleObject> list = new ArrayList<AnotherSimpleObject>();
		list.add(new AnotherSimpleObject("xpto1"));
		list.add(new AnotherSimpleObject("xpto2"));
		object.setaList(list);

		r.save(object);

		r.delete(object.getId(), SimpleObject.class);

		assertNull(r.find(SimpleObject.class, object.getId()).now());
		assertEquals(0, r.query(AnotherSimpleObject.class).parentKey(object.getKey()).asList().size());
	}
}
