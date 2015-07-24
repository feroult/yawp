package io.yawp.repository.query;

import static io.yawp.repository.query.condition.Condition.and;
import static io.yawp.repository.query.condition.Condition.c;
import static io.yawp.repository.query.condition.Condition.or;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class DatastoreQueryTest extends EndpointTestCase {

	private void saveManyBasicObjects(int n, String stringValue) {
		for (int i = 0; i < n; i++) {
			saveOneObject(stringValue, i);
		}
	}

	private IdRef<BasicObject> saveOneObject(String stringValue, int i) {
		BasicObject object = new BasicObject();
		object.setStringValue(stringValue);
		object.setIntValue(i + 1);
		yawp.save(object);
		return object.getId();
	}

	public void saveManyBasicObjects(int n) {
		saveManyBasicObjects(n, "xpto");
	}

	@Test
	public void testWhere() {
		saveManyBasicObjects(3);
		yawp.save(new BasicObject("different"));

		List<BasicObject> objects = yawp(BasicObject.class).where("stringValue", "=", "xpto").list();

		assertEquals(3, objects.size());

		assertEquals(1, objects.get(0).getIntValue());
		assertEquals(2, objects.get(1).getIntValue());
		assertEquals(3, objects.get(2).getIntValue());
	}

	@Test
	public void testWithIdAsString() {
		BasicObject myObj = new BasicObject("xpto");
		yawp.save(myObj);

		BasicObject fetch = yawp(BasicObject.class).where("id", "=", myObj.getId().toString()).only();
		assertEquals("xpto", fetch.getStringValue());
	}

	@Test
	public void testWithIdAsStringIn() {
		BasicObject myObj1 = new BasicObject("xpto1");
		yawp.save(myObj1);

		BasicObject myObj2 = new BasicObject("xpto2");
		yawp.save(myObj2);

		List<BasicObject> objects = yawp(BasicObject.class)
				.where("id", "in", Arrays.asList(myObj1.getId().toString(), myObj2.getId().toString())).order("stringValue").list();
		assertEquals(2, objects.size());

		assertEquals("xpto1", objects.get(0).getStringValue());
		assertEquals("xpto2", objects.get(1).getStringValue());
	}

	@Test
	public void testWhereWithUnicode() {
		yawp.save(new BasicObject("\u00c1"));

		List<BasicObject> objects = yawp(BasicObject.class).where("stringValue", "=", "\u00c1").list();

		assertEquals(1, objects.size());
		assertEquals("\u00c1", objects.get(0).getStringValue());
	}

	@Test
	public void testChainedWheres() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "=", 1).where("stringValue", "=", "xpto").list();

		assertEquals(1, objects.size());
		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(1, objects.get(0).getIntValue());
	}

	@Test
	public void testSimpleWhereWithNot() {
		saveManyBasicObjects(2);

		List<BasicObject> objects = yawp(BasicObject.class).where(c("intValue", "=", 1).not()).list();

		assertEquals(1, objects.size());
		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(2, objects.get(0).getIntValue());
	}

	@Test
	public void testWhereWithOrNot() {
		saveManyBasicObjects(3);

		List<BasicObject> objects = yawp(BasicObject.class).where(or(c("intValue", "=", 1), c("intValue", "=", 2)).not()).list();

		assertEquals(1, objects.size());
		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(3, objects.get(0).getIntValue());
	}

	@Test
	public void testWhereWithAndNot() {
		saveManyBasicObjects(5);

		List<BasicObject> objects = yawp(BasicObject.class).where(and(c("intValue", ">", 1), c("intValue", "<", 5)).not()).list();

		assertEquals(2, objects.size());
		sort(objects);
		assertEquals(1, objects.get(0).getIntValue());
		assertEquals(5, objects.get(1).getIntValue());
	}

	@Test
	public void testChainedWheresWithAnd() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = yawp(BasicObject.class).where(and(c("intValue", "=", 1), c("stringValue", "=", "xpto"))).list();

		assertEquals(1, objects.size());
		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(1, objects.get(0).getIntValue());
	}

	@Test
	public void testWhereWithOr() {
		saveManyBasicObjects(2);

		List<BasicObject> objects = yawp(BasicObject.class).where(or(c("intValue", "=", 1), c("intValue", "=", 2))).list();

		assertEquals(2, objects.size());
		sort(objects);

		assertEquals(2, objects.size());

		assertEquals("xpto", objects.get(0).getStringValue());
		assertEquals(1, objects.get(0).getIntValue());

		assertEquals("xpto", objects.get(1).getStringValue());
		assertEquals(2, objects.get(1).getIntValue());
	}

	private void sort(List<BasicObject> objects) {
		Collections.sort(objects, new Comparator<BasicObject>() {
			@Override
			public int compare(BasicObject o1, BasicObject o2) {
				return o1.getIntValue() - o2.getIntValue();
			}
		});
	}

	@Test
	public void testWhereWithComplexAndOrStructure() {
		saveManyBasicObjects(3);

		List<BasicObject> objects1 = yawp(BasicObject.class).where(
				or(and(c("intValue", "=", 1), c("intValue", "=", 2)), and(c("intValue", "=", 3), c("intValue", "=", 3)))).list();

		assertEquals(1, objects1.size());
		assertEquals(3, objects1.get(0).getIntValue());

		BaseCondition condition = or(and(c("intValue", "=", 3), c("intValue", "=", 3)), and(c("intValue", "=", 1), c("intValue", "=", 2)));
		List<BasicObject> objects2 = yawp(BasicObject.class).where(condition).list();

		assertEquals(1, objects2.size());
		assertEquals(3, objects2.get(0).getIntValue());
	}

	@Test
	public void testChainedWheresMultipleStatements() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "=", 1).where("stringValue", "=", "xpto").list();

		assertEquals(1, objects.size());
		assertEquals(1, objects.get(0).getIntValue());
		assertEquals("xpto", objects.get(0).getStringValue());
	}

	@Test
	public void testQueryFromOptions() {
		saveManyBasicObjects(3);

		DatastoreQueryOptions options = DatastoreQueryOptions
				.parse("{where: ['stringValue', '=', 'xpto'], order: [{p: 'intValue', d: 'desc'}], limit: 2}");

		List<BasicObject> objects = yawp(BasicObject.class).options(options).list();

		assertEquals(2, objects.size());
		assertEquals(3, objects.get(0).getIntValue());
		assertEquals(2, objects.get(1).getIntValue());
	}

	@Test
	public void testOrderWithUnicode() {
		yawp.save(new BasicObject("\u00e1"));
		yawp.save(new BasicObject("\u00e9"));
		yawp.save(new BasicObject("\u00ed"));

		List<BasicObject> objects = yawp(BasicObject.class).order("stringValue", "desc").list();

		assertEquals(3, objects.size());
		assertEquals("\u00ed", objects.get(0).getStringValue());
		assertEquals("\u00e9", objects.get(1).getStringValue());
		assertEquals("\u00e1", objects.get(2).getStringValue());
	}

	@Test
	public void testOrderWithTwoProperties() {
		saveManyBasicObjects(2, "xpto1");
		saveManyBasicObjects(2, "xpto2");

		List<BasicObject> objects = yawp(BasicObject.class).order("stringValue", "desc").order("intValue", "desc").list();

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

		List<BasicObject> objects = yawp(BasicObject.class).sort("stringValue", "desc").sort("intValue", "desc").list();

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

		List<BasicObject> objects = yawp(BasicObject.class).order("intValue", "desc").limit(1).list();

		assertEquals(1, objects.size());
		assertEquals(3, objects.get(0).getIntValue());
	}

	@Test
	public void testCursor() {
		saveManyBasicObjects(3);

		DatastoreQuery<BasicObject> q = yawp(BasicObject.class).order("intValue", "desc").limit(1);

		List<BasicObject> objects1 = q.list();
		assertEquals(3, objects1.get(0).getIntValue());

		List<BasicObject> objects2 = q.list();
		assertEquals(2, objects2.get(0).getIntValue());

		List<BasicObject> objects3 = yawp(BasicObject.class).cursor(q.getCursor()).order("intValue", "desc").limit(1).list();
		assertEquals(1, objects3.get(0).getIntValue());
	}

	@Test
	public void testFindByIdUsingWhere() {
		BasicObject object = new BasicObject("xpto");

		yawp.save(object);

		BasicObject retrievedObject = yawp(BasicObject.class).where("id", "=", object.getId()).first();
		assertEquals("xpto", retrievedObject.getStringValue());
	}

	@Test
	public void testFindByIdUsingWhereIn() {
		BasicObject object1 = new BasicObject("xpto1");
		yawp.save(object1);

		BasicObject object2 = new BasicObject("xpto2");
		yawp.save(object2);

		final List<IdRef<BasicObject>> ids = Arrays.asList(object1.getId(), object2.getId());
		List<BasicObject> objects = yawp(BasicObject.class).where("id", "in", ids).order("stringValue").list();
		assertEquals(2, objects.size());
		assertEquals("xpto1", objects.get(0).getStringValue());
		assertEquals("xpto2", objects.get(1).getStringValue());
	}

	@Test
	public void testWhereInWithEmptyList() {
		saveManyBasicObjects(1);

		List<BasicObject> objects = yawp(BasicObject.class).where("intValue", "in", Collections.emptyList()).list();

		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListOrTrueExpression() {
		saveManyBasicObjects(3);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = or(emptyListCondition, c("stringValue", "=", "xpto"));

		List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
		assertEquals(3, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListOrFalseExpression() {
		saveManyBasicObjects(1);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = or(emptyListCondition, c("stringValue", "=", "otpx"));

		List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListAndTrueExpression() {
		saveManyBasicObjects(1);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = and(emptyListCondition, c("stringValue", "=", "xpto"));

		List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListAndFalseExpression() {
		saveManyBasicObjects(1);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = and(emptyListCondition, c("stringValue", "=", "otpx"));

		List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testWhereInWithEmptyListOrComplexExpression() {
		saveManyBasicObjects(1);
		BaseCondition emptyListCondition = c("intValue", "in", Collections.emptyList());
		BaseCondition condition = and(emptyListCondition, and(c("stringValue", "=", "otpx"), emptyListCondition));

		List<BasicObject> objects = yawp(BasicObject.class).where(condition).list();
		assertEquals(0, objects.size());
	}

	@Test
	public void testIds() {
		saveManyBasicObjects(3);
		yawp.save(new BasicObject("different"));

		List<IdRef<BasicObject>> objects = yawp(BasicObject.class).where("stringValue", "=", "xpto").ids();
		assertEquals(3, objects.size());
	}

	@Test
	public void testOnlyId() {
		Long firstId = saveOneObject("xpto", 10).asLong();

		IdRef<BasicObject> id = yawp(BasicObject.class).where("stringValue", "=", "xpto").onlyId();
		assertEquals(firstId, id.asLong());
	}

	@Test
	public void testOnlyIdNoResult() {
		try {
			yawp(BasicObject.class).where("stringValue", "=", "xpto").onlyId();
		} catch (NoResultException ex) {
			return;
		}
		assertTrue(false);
	}

	@Test
	public void testOnlyIdMoreThanOneResult() {
		saveManyBasicObjects(2);
		try {
			yawp(BasicObject.class).where("stringValue", "=", "xpto").onlyId();
		} catch (MoreThanOneResultException ex) {
			return;
		}
		assertTrue(false);
	}

	@Test
	public void testWhereWithoutIndex() {
		yawp.save(new BasicObject("a", 1l));
		yawp.save(new BasicObject("b", 2l));

		List<BasicObject> objects = yawp(BasicObject.class).where("longValue", "=", 1l).list();
		assertObjects(objects, "a");
	}

	@Test
	public void testWhereAndWithoutIndex() {
		yawp.save(new BasicObject("a", 1l));
		yawp.save(new BasicObject("a", 2l));
		yawp.save(new BasicObject("b", 1l));

		List<BasicObject> objects = yawp(BasicObject.class).where(c("stringValue", "=", "a").and(c("longValue", "=", 1l))).list();
		assertObjects(objects, "a");
	}

	@Test
	public void testWhereOrWithoutIndex() {
		yawp.save(new BasicObject("a", 1l));
		yawp.save(new BasicObject("b", 2l));
		yawp.save(new BasicObject("c", 1l));

		List<BasicObject> objects = yawp(BasicObject.class).where(c("stringValue", "=", "a").or(c("longValue", "=", 1l))).list();
		assertObjects(objects, "a", "c");
	}

	private void assertObjects(List<BasicObject> objects, String... strings) {
		assertEquals(strings.length, objects.size());
		for (int i = 0; i < strings.length; i++) {
			assertEquals(strings[i], objects.get(i).getStringValue());
		}
	}
}
