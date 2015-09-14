package io.yawp.driver.mock;

import io.yawp.driver.api.QueryDriver;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.FalsePredicateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MockQueryDriver implements QueryDriver {

	@Override
	public <T> List<T> objects(QueryBuilder<?> builder) throws FalsePredicateException {
		return generateResults(builder);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) throws FalsePredicateException {
		List<IdRef<T>> ids = new ArrayList<IdRef<T>>();

		List<Object> objects = generateResults(builder);
		for (Object object : objects) {
			ObjectHolder objectHolder = new ObjectHolder(object);
			ids.add((IdRef<T>) objectHolder.getId());
		}

		return ids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fetch(IdRef<T> id) {
		return (T) MockStore.get(id);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> generateResults(QueryBuilder<?> builder) {
		List<Object> objects = queryWhere(builder);

		sortList(builder, objects);

		return (List<T>) objects;
	}

	private List<Object> queryWhere(QueryBuilder<?> builder) {
		List<Object> objectsInStore = MockStore.list(builder.getModel().getClazz(), builder.getParentId());

		BaseCondition condition = builder.getCondition();
		List<Object> result = new ArrayList<Object>();

		for (Object object : objectsInStore) {
			if (!condition.evaluate(object)) {
				continue;
			}

			result.add(object);
		}
		return result;
	}

	public void sortList(QueryBuilder<?> builder, List<?> objects) {
		final List<QueryOrder> preOrders = builder.getPreOrders();

		if (preOrders.size() == 0) {
			return;
		}

		Collections.sort(objects, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				for (QueryOrder order : preOrders) {
					int compare = order.compare(o1, o2);

					if (compare == 0) {
						continue;
					}

					return compare;
				}
				return 0;
			}
		});
	}
}
