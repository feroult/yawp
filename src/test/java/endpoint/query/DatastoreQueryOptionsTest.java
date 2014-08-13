package endpoint.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import endpoint.query.Condition.SimpleCondition;

public class DatastoreQueryOptionsTest {

	@Test
	public void testEmpty() {
		String q = "{}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertNull(options.getWhere());
		assertNull(options.getPreOrders());
		assertNull(options.getPostOrders());
		assertNull(options.getLimit());
	}

	@Test
	public void testQueryOptions() {
		String q = "{where: ['aLong', '=', 1, 'aInt', '=', 3, 'aDouble', '=', 4.3], order: [{p:'aString', d:'desc'}], sort: [{p:'aLong', d:'desc'}], limit: 2}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertArrayEquals(new Object[] { "aLong", "=", 1l, "aInt", "=", 3l, "aDouble", "=", 4.3 }, options.getWhere());
		assertOrderEquals("aString", "desc", options.getPreOrders().get(0));
		assertOrderEquals("aLong", "desc", options.getPostOrders().get(0));
		assertEquals(new Integer(2), options.getLimit());
	}

	@Test
	@Ignore
	public void testWhereSimpleCondition() {
		String q = "{p: 'aLong', op: '=', v: 1}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertEquals(SimpleCondition.class, options.getCondition().getClass());

		SimpleCondition condition = (SimpleCondition) options.getCondition();
		assertEquals("aLong", condition.getField());
		assertEquals("=", condition.getOperator());
		assertEquals(1l, condition.getValue());
	}

	@Test
	@Ignore
	public void testWhereTwoConditions() {
		String q = "{op: 'and', c: [{p: 'aLong', op: '=', v: 1}, {p: 'aInt', op: '=', v: 3}]}";
	}

	@Test
	@Ignore
	public void testWhereThreeConditionsWithPrecedence() {
		String q = "{op: 'and', c: [{p: 'aLong', op: '=', v: 1}, {op: 'or', c: [{p: 'aInt', op: '=', v: 3}, {p: 'aDouble', op: '=', v: 4.3}]}}";
	}

	private void assertOrderEquals(String property, String direction, DatastoreQueryOrder order) {
		assertEquals(property, order.getProperty());
		assertEquals(direction, order.getDirection());
	}
}
