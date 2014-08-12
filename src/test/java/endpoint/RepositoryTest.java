package endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;

public class RepositoryTest extends EndpointTestCase {

	@Test
	public void testSave() throws HttpException {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");

		r.save(object);
		object = r.query(SimpleObject.class).id(object.getId());

		object.assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testJsonProperty() throws HttpException {
		SimpleObject object = new SimpleObject();

		object.setAString("xpto");
		object.setNotADatastoreObject(new NotADatastoreObject("xpto"));

		r.save(object);

		object = r.query(SimpleObject.class).id(object.getId());

		assertEquals("xpto", object.getAString());
		assertEquals("xpto", object.getNotADatastoreObject().getName());
	}

	@Test
	public void testJsonArrayProperty() throws HttpException {
		SimpleObject object = new SimpleObject();

		object.setAString("xpto");

		List<NotADatastoreObject> list = new ArrayList<NotADatastoreObject>();

		list.add(new NotADatastoreObject("xpto1"));
		list.add(new NotADatastoreObject("xpto2"));

		object.setNotADatastoreObjectList(list);

		r.save(object);

		object = r.query(SimpleObject.class).id(object.getId());

		assertEquals("xpto", object.getAString());
		assertEquals("xpto1", object.getNotADatastoreObjectList().get(0).getName());
		assertEquals("xpto2", object.getNotADatastoreObjectList().get(1).getName());
	}

	@Test
	public void testJsonMapWithLongKeyAndObjectValue() throws HttpException {
		SimpleObject object = new SimpleObject();

		Map<Long, NotADatastoreObject> map = new HashMap<Long, NotADatastoreObject>();

		map.put(1l, new NotADatastoreObject("xpto1"));
		map.put(2l, new NotADatastoreObject("xpto2"));

		object.setAMap(map);

		r.save(object);

		object = r.query(SimpleObject.class).id(object.getId());
		assertEquals("xpto1", object.getAMap().get(1l).getName());
		assertEquals("xpto2", object.getAMap().get(2l).getName());
	}

	@Test(expected = NoResultException.class)
	public void testDelete() throws HttpException {
		SimpleObject object = new SimpleObject("xpto");

		List<AnotherSimpleObject> list = new ArrayList<AnotherSimpleObject>();
		list.add(new AnotherSimpleObject("xpto1"));
		list.add(new AnotherSimpleObject("xpto2"));

		r.save(object);
		r.delete(object.getId(), SimpleObject.class);
		r.query(SimpleObject.class).id(object.getId());
	}
}
