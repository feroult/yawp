package endpoint.query;

import static endpoint.query.Condition.and;
import static endpoint.query.Condition.c;
import static endpoint.query.Condition.or;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import endpoint.HttpException;
import endpoint.Id;
import endpoint.SimpleObject;
import endpoint.query.DatastoreQuery;
import endpoint.query.DatastoreQueryOptions;
import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;

public class DatastoreQueryTest extends EndpointTestCase {

	private void saveThreeObjects() {
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1"));
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object3"));
	}

	@Test
	public void testWhere() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object4"));

		List<SimpleObject> objects = r.query(SimpleObject.class).where("aLong", "=", 1l).list();

		assertEquals(3, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
		objects.get(2).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");
	}

	@Test
	public void testWhereWithUnicode() {
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "\u00c1"));

		List<SimpleObject> objects = r.query(SimpleObject.class).where("aString", "=", "\u00c1").list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "\u00c1");
	}

	@Test
	public void testChainedWheres() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));

		@SuppressWarnings("deprecation")
		List<SimpleObject> objects = r.query(SimpleObject.class).where("aLong", "=", 1l, "aString", "=", "object2").list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
	}
	
	@Test
	public void testChainedWheresWithNewAPI() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));

		List<SimpleObject> objects = r.query(SimpleObject.class).where(and(c("aLong", "=", 1l), c("aString", "=", "object2"))).list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
	}
	
	@Test
	public void testWhereWithOr() {
		saveThreeObjects();
		List<SimpleObject> objects = r.query(SimpleObject.class).where(or(c("aString", "=", "object1"), c("aString", "=", "object2"))).list();

		assertEquals(2, objects.size());
		Collections.sort(objects, new Comparator<SimpleObject>() {
			@Override
			public int compare(SimpleObject o1, SimpleObject o2) {
				return o1.getAString().compareTo(o2.getAString());
			}
		});

		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
	}

	@Test
	public void testWhereWithComplexAndOrStructure() {
		saveThreeObjects();
		List<SimpleObject> objects = r.query(SimpleObject.class).where(or(and(c("aString", "=", "object2"), c("aString", "=", "object1")), and(c("aString", "=", "object2"), c("aString", "=", "object2")))).list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
		
		objects = r.query(SimpleObject.class).where(or(and(c("aString", "=", "object1"), c("aString", "=", "object1")), and(c("aString", "=", "object2"), c("aString", "=", "object1")))).list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testChainedWheresMultipleStatements() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));

		List<SimpleObject> objects = r.query(SimpleObject.class).where("aLong", "=", 1l).where("aString", "=", "object2").list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
	}

	@Test
	public void testOptions() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object4"));

		DatastoreQueryOptions options = DatastoreQueryOptions
				.parse("{where: ['aLong', '=', 1], order: [{p:'aString', d:'desc'}], limit: 2}");

		List<SimpleObject> objects = r.query(SimpleObject.class).options(options).list();

		assertEquals(2, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
	}

	@Test
	public void testOrderWithUnicode() {
		saveThreeObjects();
		// 'A' with accent
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "\u00c1"));

		List<SimpleObject> objects = r.query(SimpleObject.class).order("aString", "desc").list();

		assertEquals(4, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
		objects.get(2).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
		objects.get(3).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "\u00c1");
	}

	@Test
	public void testOrderWithTwoProperties() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));
		r.save(new SimpleObject(1, 3l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object3"));

		List<SimpleObject> objects = r.query(SimpleObject.class).order("aString", "desc").order("aLong", "desc").list();

		objects.get(0).assertObject(1, 3l, 1.1, true, "2013/12/26 23:55:01", "object3");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");
		objects.get(2).assertObject(1, 2l, 1.1, true, "2013/12/26 23:55:01", "object2");
		objects.get(3).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
		objects.get(4).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testSort() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));
		r.save(new SimpleObject(1, 3l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object3"));

		List<SimpleObject> objects = r.query(SimpleObject.class).sort("aString", "desc").sort("aLong", "desc").list();

		objects.get(0).assertObject(1, 3l, 1.1, true, "2013/12/26 23:55:01", "object3");
		objects.get(1).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");
		objects.get(2).assertObject(1, 2l, 1.1, true, "2013/12/26 23:55:01", "object2");
		objects.get(3).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");
		objects.get(4).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testLimit() {
		saveThreeObjects();

		List<SimpleObject> objects = r.query(SimpleObject.class).order("aString", "desc").limit(1).list();

		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");
	}

	@Test
	public void testCursor() {
		saveThreeObjects();

		DatastoreQuery<SimpleObject> q = r.query(SimpleObject.class).order("aString", "desc").limit(1);

		List<SimpleObject> objects = q.list();
		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object3");

		objects = q.list();
		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object2");

		objects = r.query(SimpleObject.class).cursor(q.getCursor()).order("aString", "desc").limit(1).list();
		assertEquals(1, objects.size());
		objects.get(0).assertObject(1, 1l, 1.1, true, "2013/12/26 23:55:01", "object1");
	}

	@Test
	public void testFindByIdUsingWhere() {
		SimpleObject object = new SimpleObject("xpto");

		r.save(object);

		object = r.query(SimpleObject.class).where("id", "=", object.getId()).first();
		assertEquals("xpto", object.getAString());
	}

	@Test
	public void testFindByIdUsingWhereWithLongId() {
		ObjectWithLongId object = new ObjectWithLongId("xpto");

		r.save(object);

		object = r.query(ObjectWithLongId.class).where("key", "=", object.key).first();
		assertEquals("xpto", object.text);
	}

	@Test
	public void testFindById() {
		SimpleObject object = new SimpleObject("xpto");

		r.save(object);

		object = r.query(SimpleObject.class).id(object.getId());
		assertEquals("xpto", object.getAString());
	}

	public static class ObjectWithLongId {

		@Id
		Long key;

		String text;

		public ObjectWithLongId() {
		}

		public ObjectWithLongId(String text) {
			this.text = text;
		}
	}

}
