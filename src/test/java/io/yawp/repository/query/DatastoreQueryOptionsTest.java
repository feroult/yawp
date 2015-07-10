package io.yawp.repository.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.JoinedCondition;
import io.yawp.repository.query.condition.LogicalOperator;
import io.yawp.repository.query.condition.SimpleCondition;
import io.yawp.repository.query.condition.WhereOperator;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DatastoreQueryOptionsTest {

	@Test
	public void testEmpty() {
		String q = "{}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		assertNull(options.getPreOrders());
		assertNull(options.getPostOrders());
		assertNull(options.getLimit());
	}

	@Test
	public void testQueryOptions() {
		String q = "{where: ['longValue', '=', 1, 'intValue', '=', 3, 'doubleValue', '=', 4.3], order: [{p:'stringValue', d:'desc'}], sort: [{p:'longValue', d:'desc'}], limit: 2}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		JoinedCondition conditions = assertJoinedCondition(options.getCondition(), LogicalOperator.AND, 3);
		assertSimpleCondition(conditions.getConditions()[0], "longValue", WhereOperator.EQUAL, 1l);
		assertSimpleCondition(conditions.getConditions()[1], "intValue", WhereOperator.EQUAL, 3l);
		assertSimpleCondition(conditions.getConditions()[2], "doubleValue", WhereOperator.EQUAL, 4.3);

		List<DatastoreQueryOrder> order = options.getPreOrders();
		assertEquals(1, order.size());
		assertOrderEquals("stringValue", "desc", order.get(0));

		List<DatastoreQueryOrder> sort = options.getPostOrders();
		assertEquals(1, sort.size());
		assertOrderEquals("longValue", "desc", sort.get(0));

		assertEquals(new Integer(2), options.getLimit());
	}

	@Test
	public void testWhereSimpleCondition() {
		String q = "{where: {p: 'longValue', op: '=', v: 1}}";
		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);
		assertSimpleCondition(options.getCondition(), "longValue", WhereOperator.EQUAL, 1l);
	}

	@Test
	public void testWhereWithIn() {
		String q = "{ where: [ 'id', 'in', ['1', '3', '5'] ] }";
		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);
		assertSimpleCondition(options.getCondition(), "id", WhereOperator.IN, Arrays.asList("1", "3", "5"));
	}

	@Test
	public void testWhereJoinedConditions() {
		String q = "{where: {op: 'and', c: [{p: 'longValue', op: '=', v: 1}, {p: 'intValue', op: '=', v: 3}]}}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		JoinedCondition condition = assertJoinedCondition(options.getCondition(), LogicalOperator.AND, 2);
		assertSimpleCondition(condition.getConditions()[0], "longValue", WhereOperator.EQUAL, 1l);
		assertSimpleCondition(condition.getConditions()[1], "intValue", WhereOperator.EQUAL, 3l);
	}

	@Test
	public void testWhereJoinedConditionsWithPrecedence() {
		String q = "{where: {op: 'and', c: [{p: 'longValue', op: '=', v: 1}, {op: 'or', c: [{p: 'intValue', op: '=', v: 3}, {p: 'doubleValue', op: '=', v: 4.3}]}]}}";

		DatastoreQueryOptions options = DatastoreQueryOptions.parse(q);

		JoinedCondition condition1 = assertJoinedCondition(options.getCondition(), LogicalOperator.AND, 2);
		assertSimpleCondition(condition1.getConditions()[0], "longValue", WhereOperator.EQUAL, 1l);

		JoinedCondition condition2 = assertJoinedCondition(condition1.getConditions()[1], LogicalOperator.OR, 2);
		assertSimpleCondition(condition2.getConditions()[0], "intValue", WhereOperator.EQUAL, 3l);
		assertSimpleCondition(condition2.getConditions()[1], "doubleValue", WhereOperator.EQUAL, 4.3);
	}

	private JoinedCondition assertJoinedCondition(BaseCondition c, LogicalOperator logicalOperator, int length) {
		assertEquals(JoinedCondition.class, c.getClass());
		JoinedCondition condition = (JoinedCondition) c;
		assertEquals(logicalOperator, condition.getLogicalOperator());
		assertEquals(length, condition.getConditions().length);
		return condition;
	}

	private void assertSimpleCondition(BaseCondition c, String p, WhereOperator whereOperator, Object value) {
		assertEquals(SimpleCondition.class, c.getClass());
		SimpleCondition condition = (SimpleCondition) c;
		assertEquals(p, condition.getField());
		assertEquals(whereOperator, condition.getWhereOperator());
		assertEquals(value, condition.getValue());
	}

	private void assertOrderEquals(String property, String direction, DatastoreQueryOrder order) {
		assertEquals(property, order.getProperty());
		assertEquals(direction, order.getDirection());
	}
}
