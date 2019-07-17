package io.yawp.driver.mock;

import io.yawp.driver.api.QueryDriver;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class MockQueryDriver implements QueryDriver {

	private Repository r;

	public MockQueryDriver(Repository r) {
		this.r = r;
	}

	@Override
	public <T> List<T> objects(QueryBuilder<?> builder) {
		return generateResults(builder);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) {
		List<IdRef<T>> ids = new ArrayList<>();

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

	@Override
	public <T> Map<IdRef<T>, T> fetchAll(List<IdRef<T>> ids) {
		return ids.stream().collect(toMap(Function.identity(), this::fetch));
	}

	@Override
	public <T> FutureObject<T> fetchAsync(IdRef<T> id) {
		T object = fetch(id);
		Future<T> futureObject = ConcurrentUtils.constantFuture(object);
		return new FutureObject<>(r, futureObject);
	}

	private <T> List<T> generateResults(QueryBuilder<?> builder) {
		List<Object> objects = queryWhere(builder);

		sortList(builder, objects);

		List<T> resultFromCursor = applyCursor(builder, objects);
		List<T> result = applyLimit(builder, resultFromCursor);

		updateCursor(builder, result);

		return result;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> applyCursor(QueryBuilder<?> builder, List<Object> objects) {
		if (builder.getCursor() == null) {
			return (List<T>) objects;
		}

		List<T> result = new ArrayList<>();

		IdRef<?> cursorId = MockStore.getCursor(builder.getCursor());
		boolean startAdd = false;

		for (Object object : objects) {
			if (startAdd) {
				result.add((T) object);
				continue;
			}
			ObjectHolder objectHolder = new ObjectHolder(object);
			if (cursorId.equals(objectHolder.getId())) {
				startAdd = true;
			}
		}

		return result;
	}

	private <T> void updateCursor(QueryBuilder<?> builder, List<T> result) {
		if (result.size() == 0) {
			return;
		}
		T cursorObject = result.get(result.size() - 1);

		if (builder.getCursor() == null) {
			builder.setCursor(MockStore.createCursor(cursorObject));
		} else {
			MockStore.updateCursor(builder.getCursor(), cursorObject);
		}
	}

	private <T> List<T> applyLimit(QueryBuilder<?> builder, List<T> objects) {
		if (builder.getLimit() != null) {
			int limit = builder.getLimit() > objects.size() ? objects.size() : builder.getLimit();
			return objects.subList(0, limit);
		}

		return objects;
	}

	private List<Object> queryWhere(QueryBuilder<?> builder) {
		List<Object> objectsInStore = MockStore.list(builder.getModel().getClazz(), builder.getParentId());

		BaseCondition condition = builder.getCondition();
		List<Object> result = new ArrayList<>();

		for (Object object : objectsInStore) {
			if (condition != null && !condition.evaluate(object)) {
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

		objects.sort((o1, o2) -> {
			for (QueryOrder order : preOrders) {
				int compare = order.compare(o1, o2);

				if (compare == 0) {
					continue;
				}

				return compare;
			}
			return 0;
		});
	}
}
