package io.yawp.repository.driver.appengine;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.Repository;
import io.yawp.repository.driver.api.QueryDriver;
import io.yawp.repository.query.DatastoreQueryOrder;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.condition.FalsePredicateException;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;

public class AppengineQueryDriver implements QueryDriver {

	private Repository r;

	private QueryBuilder<?> builder;

	public AppengineQueryDriver(QueryBuilder<?> builder) {
		this.r = builder.getRepository();
		this.builder = builder;
	}
//
//	@Override
//	public <T> List<T> execute() throws FalsePredicateException {
//		QueryResultList<Entity> queryResult = generateResults(false);
//
//		List<T> objects = new ArrayList<T>();
//
//		for (Entity entity : queryResult) {
//			objects.add(EntityUtils.toObject(r, entity, clazz));
//		}
//
//		return objects;
//	}
//
//	private QueryResultList<Entity> generateResults(boolean keysOnly) throws FalsePredicateException {
//		QueryResultList<Entity> queryResult = prepareQuery(keysOnly).asQueryResultList(configureFetchOptions());
//		setCursor(queryResult);
//		return queryResult;
//	}
//
//	private PreparedQuery prepareQuery(boolean keysOnly) throws FalsePredicateException {
//		Query q = new Query(KindResolver.getKindFromClass(clazz));
//
//		if (keysOnly) {
//			q.setKeysOnly();
//		}
//
//		prepareQueryAncestor(q);
//		prepareQueryWhere(q);
//		prepareQueryOrder(q);
//
//		return r.datastore().prepare(q);
//	}
//
//	private void prepareQueryOrder(Query q) {
//		if (preOrders.isEmpty()) {
//			return;
//		}
//
//		for (DatastoreQueryOrder order : preOrders) {
//			String string = EntityUtils.getActualFieldName(order.getProperty(), clazz);
//			q.addSort(string, order.getSortDirection());
//		}
//	}
//
//	private void prepareQueryWhere(Query q) throws FalsePredicateException {
//		if (condition != null && condition.hasPreFilter()) {
//			q.setFilter(condition.createPreFilter());
//		}
//	}
//
//	private void prepareQueryAncestor(Query q) {
//		if (parentKey == null) {
//			return;
//		}
//		q.setAncestor(parentKey);
//	}
//

	@Override
	public <T> List<T> execute() throws FalsePredicateException {
		// TODO Auto-generated method stub
		return null;
	}
}
