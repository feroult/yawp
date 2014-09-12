package endpoint.repository.query;

import static endpoint.repository.query.Condition.and;
import static endpoint.repository.query.Condition.c;
import static endpoint.repository.query.Condition.or;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import endpoint.repository.SimpleObject;
import endpoint.repository.annotations.Id;
import endpoint.repository.models.basic.BasicObject;
import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;

public class NewDatastoreQueryTest extends EndpointTestCase {

	private void saveThreeObjects() {
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1"));
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object2"));
		r.save(new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object3"));
	}

	private void saveManyBasicObjects(int n, String stringValue) {
		for (int i = 0; i < n; i++) {
			BasicObject object = new BasicObject();
			object.setStringValue(stringValue);
			object.setIntValue(i + 1);
			r.save(object);
		}
	}

	public void saveManyBasicObjects(int n) {
		saveManyBasicObjects(n, "xpto");
	}

	@Test
	public void testWhere() {
		saveManyBasicObjects(3);
		r.save(new BasicObject("different"));

		List<BasicObject> objects = r.query(BasicObject.class).where("stringValue", "=", "xpto").list();

		assertEquals(3, objects.size());

		assertEquals(1, objects.get(0).getIntValue());
		assertEquals(2, objects.get(1).getIntValue());
		assertEquals(3, objects.get(2).getIntValue());
	}

	@Test
	public void testWhereWithUnicode() {
		r.save(new BasicObject("\u00c1"));

		List<BasicObject> objects = r.query(BasicObject.class).where("stringValue", "=", "\u00c1").list();

		assertEquals(1, objects.size());
		assertEquals("\u00c1", objects.get(0).getStringValue());
	}

	@Test
	public void testChainedWheres() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = r.query(BasicObject.class).where("intValue", "=", 1).where("stringValue", "=", "xpto").list();

		assertEquals(1, objects.size());
		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(1, objects.get(0).getIntValue());
	}

	@Test
	public void testChainedWheresWithAnd() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = r.query(BasicObject.class).where(and(c("intValue", "=", 1), c("stringValue", "=", "xpto"))).list();

		assertEquals(1, objects.size());
		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(1, objects.get(0).getIntValue());
	}

	@Test
	public void testWhereWithOr() {
		saveManyBasicObjects(2);

		List<BasicObject> objects = r.query(BasicObject.class).where(or(c("intValue", "=", 1), c("intValue", "=", 2))).list();

		assertEquals(2, objects.size());
		Collections.sort(objects, new Comparator<BasicObject>() {
			@Override
			public int compare(BasicObject o1, BasicObject o2) {
				return o1.getIntValue() - o2.getIntValue();
			}
		});

		assertEquals(2, objects.size());

		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(1, objects.get(0).getIntValue());

		assertEquals("xpto", objects.get(1).getStringValue());
		assertEquals(2, objects.get(1).getIntValue());
	}

	@Test
	public void testWhereWithComplexAndOrStructure() {
		saveManyBasicObjects(3);

		List<BasicObject> objects1 = r.query(BasicObject.class)
				.where(or(and(c("intValue", "=", 1), c("intValue", "=", 2)), and(c("intValue", "=", 3), c("intValue", "=", 3)))).list();

		assertEquals(1, objects1.size());
		assertEquals(3, objects1.get(0).getIntValue());

		List<BasicObject> objects2 = r.query(BasicObject.class)
				.where(or(and(c("intValue", "=", 3), c("intValue", "=", 3)), and(c("intValue", "=", 1), c("intValue", "=", 2)))).list();

		assertEquals(1, objects2.size());
		assertEquals(3, objects2.get(0).getIntValue());
	}

	@Test
	public void testChainedWheresMultipleStatements() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = r.query(BasicObject.class).where("intValue", "=", 1).where("stringValue", "=", "xpto").list();

		assertEquals(1, objects.size());
		assertEquals(1, objects.get(0).getIntValue());
		assertEquals("xpto", objects.get(0).getStringValue());
	}

	@Test
	public void testQueryFromOptions() {
		saveManyBasicObjects(3);

		DatastoreQueryOptions options = DatastoreQueryOptions
				.parse("{where: ['stringValue', '=', 'xpto'], order: [{p: 'intValue', d: 'desc'}], limit: 2}");

		List<BasicObject> objects = r.query(BasicObject.class).options(options).list();

		assertEquals(2, objects.size());
		assertEquals(3, objects.get(0).getIntValue());
		assertEquals(2, objects.get(1).getIntValue());
	}

	@Test
	public void testOrderWithUnicode() {
		r.save(new BasicObject("\u00e1"));
		r.save(new BasicObject("\u00e9"));
		r.save(new BasicObject("\u00ed"));

		List<BasicObject> objects = r.query(BasicObject.class).order("stringValue", "desc").list();

		assertEquals(3, objects.size());
		assertEquals("\u00ed", objects.get(0).getStringValue());
		assertEquals("\u00e9", objects.get(1).getStringValue());
		assertEquals("\u00e1", objects.get(2).getStringValue());
	}

	@Test
	public void testOrderWithTwoProperties() {
		saveManyBasicObjects(2, "xpto1");
		saveManyBasicObjects(2, "xpto2");

		List<BasicObject> objects = r.query(BasicObject.class).order("stringValue", "desc").order("intValue", "desc").list();

		assertEquals(4, objects.size());

		assertEquals("xpto2", objects.get(0).getStringValue());
		assertEquals("xpto2", objects.get(1).getStringValue());
		assertEquals("xpto1", objects.get(2).getStringValue());
		assertEquals("xpto1", objects.get(3).getStringValue());

		assertEquals(2, objects.get(0).getIntValue());
		assertEquals(1, objects.get(1).getIntValue());
		assertEquals(2, objects.get(2).getIntValue());
		assertEquals(1, objects.get(3).getIntValue());
	}

	@Test
	public void testSortWithTwoProperties() {
		saveManyBasicObjects(2, "xpto1");
		saveManyBasicObjects(2, "xpto2");

		List<BasicObject> objects = r.query(BasicObject.class).sort("stringValue", "desc").sort("intValue", "desc").list();

		assertEquals(4, objects.size());

		assertEquals("xpto2", objects.get(0).getStringValue());
		assertEquals("xpto2", objects.get(1).getStringValue());
		assertEquals("xpto1", objects.get(2).getStringValue());
		assertEquals("xpto1", objects.get(3).getStringValue());

		assertEquals(2, objects.get(0).getIntValue());
		assertEquals(1, objects.get(1).getIntValue());
		assertEquals(2, objects.get(2).getIntValue());
		assertEquals(1, objects.get(3).getIntValue());
	}

	@Test
	public void testLimit() {
		saveManyBasicObjects(3);

		List<BasicObject> objects = r.query(BasicObject.class).order("intValue", "desc").limit(1).list();

		assertEquals(1, objects.size());
		assertEquals(3, objects.get(0).getIntValue());
	}

	@Test
	public void testCursor() {
		saveManyBasicObjects(3);

		DatastoreQuery<BasicObject> q = r.query(BasicObject.class).order("intValue", "desc").limit(1);

		List<BasicObject> objects1 = q.list();
		assertEquals(3, objects1.get(0).getIntValue());

		List<BasicObject> objects2 = q.list();
		assertEquals(2, objects2.get(0).getIntValue());

		List<BasicObject> objects3 = r.query(BasicObject.class).cursor(q.getCursor()).order("intValue", "desc").limit(1).list();
		assertEquals(1, objects3.get(0).getIntValue());
	}

	@Test
	public void testFindByIdUsingWhere() {
		BasicObject object = new BasicObject("xpto");

		r.save(object);

		BasicObject retrievedObject = r.query(BasicObject.class).where("id", "=", object.getId()).first();
		assertEquals("xpto", retrievedObject.getStringValue());
	}

	@Test
	public void testFindByIdUsingWhereIn() {
		BasicObject object1 = new BasicObject("xpto1");
		r.save(object1);

		BasicObject object2 = new BasicObject("xpto2");
		r.save(object2);

		List<BasicObject> objects = r.query(BasicObject.class).where("id", "in", Arrays.asList(object1.getId())).list();
		assertEquals(1, objects.size());
	}

	@Test
	public void testWhereInWithEmptyList() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = r.query(BasicObject.class).where("intValue", "in", Collections.emptyList()).list();

		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListOrTrueExpression() {
		saveManyBasicObjects(3);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = or(emptyListCondition, c("stringValue", "=", "xpto"));

		List<BasicObject> objects = r.query(BasicObject.class).where(condition).list();
		assertEquals(3, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListOrFalseExpression() {
		saveManyBasicObjects(3);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = or(emptyListCondition, c("stringValue", "=", "otpx"));

		List<BasicObject> objects = r.query(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListAndTrueExpression() {
		saveManyBasicObjects(3);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = and(emptyListCondition, c("stringValue", "=", "xpto"));

		List<BasicObject> objects = r.query(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListAndFalseExpression() {
		saveManyBasicObjects(3);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = and(emptyListCondition, c("stringValue", "=", "otpx"));

		List<BasicObject> objects = r.query(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInEmptyListWithinAndAndOr() {
		saveThreeObjects();
		r.save(new SimpleObject(1, 2l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object4"));
		BaseCondition falseCondition = Condition.c("aLong", "in", Collections.emptyList());
		BaseCondition condition = Condition.or(falseCondition, Condition.and(c("aLong", "=", 1l), falseCondition));

		List<SimpleObject> objects = r.query(SimpleObject.class).where(condition).list();
		assertEquals(0, objects.size());
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
