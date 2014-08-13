package endpoint.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import com.google.appengine.api.datastore.Query.FilterOperator;

import endpoint.query.Condition.JoinedCondition;
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
	public void testWhereSimpleCondition() {
		String q = "{where: {p: 'aLong', op: '=', v: 1}}";
		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);
		assertSimpleCondition(options.getCondition(), "aLong", FilterOperator.EQUAL, 1l);
	}

	@Test
	public void testWhereJoinedConditions() {
		String q = "{where: {op: 'and', c: [{p: 'aLong', op: '=', v: 1}, {p: 'aInt', op: '=', v: 3}]}}";
		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		JoinedCondition condition = assertJoinedCondition(options.getCondition(), LogicalOperator.AND, 2);
		
		assertSimpleCondition(condition.getConditions()[0], "aLong", FilterOperator.EQUAL, 1l);
		assertSimpleCondition(condition.getConditions()[1], "aInt", FilterOperator.EQUAL, 3l);
	}

	@Test
	@Ignore
	public void testWhereJoinedConditionsWithPrecedence() {
		String q = "{where: {op: 'and', c: [{p: 'aLong', op: '=', v: 1}, {op: 'or', c: [{p: 'aInt', op: '=', v: 3}, {p: 'aDouble', op: '=', v: 4.3}]}}}";
	}
	
	private JoinedCondition assertJoinedCondition(Condition c, LogicalOperator operator, int length) {
		assertEquals(JoinedCondition.class, c.getClass());
		JoinedCondition condition = (JoinedCondition) c;
		assertEquals(operator, condition.getOperator());
		assertEquals(length, condition.getConditions().length);
		return condition;
	}

	private void assertSimpleCondition(Condition c, String p, FilterOperator op, long value) {
		assertEquals(SimpleCondition.class, c.getClass());
		SimpleCondition condition = (SimpleCondition) c;
		assertEquals(p, condition.getField());
		assertEquals(op, condition.getOperator());
		assertEquals(value, condition.getValue());
	}

	private void assertOrderEquals(String property, String direction, DatastoreQueryOrder order) {
		assertEquals(property, order.getProperty());
		assertEquals(direction, order.getDirection());
	}
}
