package endpoint.repository.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.appengine.api.datastore.Query.FilterOperator;

import endpoint.repository.query.BaseCondition.JoinedCondition;
import endpoint.repository.query.BaseCondition.SimpleCondition;

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
		String q = "{where: ['longValue', '=', 1, 'intValue', '=', 3, 'doubleValue', '=', 4.3], order: [{p:'stringValue', d:'desc'}], sort: [{p:'longValue', d:'desc'}], limit: 2}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertArrayEquals(new Object[] { "longValue", "=", 1l, "intValue", "=", 3l, "doubleValue", "=", 4.3 }, options.getWhere());
		assertOrderEquals("stringValue", "desc", options.getPreOrders().get(0));
		assertOrderEquals("longValue", "desc", options.getPostOrders().get(0));
		assertEquals(new Integer(2), options.getLimit());
	}

	@Test
	public void testWhereSimpleCondition() {
		String q = "{where: {p: 'longValue', op: '=', v: 1}}";
		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);
		assertSimpleCondition(options.getCondition(), "longValue", FilterOperator.EQUAL, 1l);
	}

	@Test
	public void testWhereJoinedConditions() {
		String q = "{where: {op: 'and', c: [{p: 'longValue', op: '=', v: 1}, {p: 'intValue', op: '=', v: 3}]}}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		JoinedCondition condition = assertJoinedCondition(options.getCondition(), LogicalOperator.AND, 2);
		assertSimpleCondition(condition.getConditions()[0], "longValue", FilterOperator.EQUAL, 1l);
		assertSimpleCondition(condition.getConditions()[1], "intValue", FilterOperator.EQUAL, 3l);
	}

	@Test
	public void testWhereJoinedConditionsWithPrecedence() {
		String q = "{where: {op: 'and', c: [{p: 'longValue', op: '=', v: 1}, {op: 'or', c: [{p: 'intValue', op: '=', v: 3}, {p: 'doubleValue', op: '=', v: 4.3}]}]}}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		JoinedCondition condition1 = assertJoinedCondition(options.getCondition(), LogicalOperator.AND, 2);
		assertSimpleCondition(condition1.getConditions()[0], "longValue", FilterOperator.EQUAL, 1l);

		JoinedCondition condition2 = assertJoinedCondition(condition1.getConditions()[1], LogicalOperator.OR, 2);
		assertSimpleCondition(condition2.getConditions()[0], "intValue", FilterOperator.EQUAL, 3l);
		assertSimpleCondition(condition2.getConditions()[1], "doubleValue", FilterOperator.EQUAL, 4.3);
	}

	private JoinedCondition assertJoinedCondition(BaseCondition c, LogicalOperator operator, int length) {
		assertEquals(JoinedCondition.class, c.getClass());
		JoinedCondition condition = (JoinedCondition) c;
		assertEquals(operator, condition.getOperator());
		assertEquals(length, condition.getConditions().length);
		return condition;
	}

	private void assertSimpleCondition(BaseCondition c, String p, FilterOperator op, Object value) {
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
