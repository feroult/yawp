package io.yawp.repository.query;

import io.yawp.repository.IdRef;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.transformers.RepositoryTransformers;

import java.util.ArrayList;
import java.util.List;

public class DatastoreQueryTransformer<F, T> {

	private QueryBuilder<F> query;

	private String transformName;

	public DatastoreQueryTransformer(QueryBuilder<F> query, String transformName) {
		this.query = query;
		this.transformName = transformName;
	}

	@Deprecated
	public DatastoreQueryTransformer<F, T> where(Object... values) {
		query.where(values);
		return this;
	}

	public DatastoreQueryTransformer<F, T> where(String field, String operator, Object value) {
		query.where(field, operator, value);
		return this;
	}

	public DatastoreQueryTransformer<F, T> where(BaseCondition c) {
		query.where(c);
		return this;
	}

	public DatastoreQueryTransformer<F, T> order(String property) {
		order(property, null);
		return this;
	}

	public DatastoreQueryTransformer<F, T> order(String property, String direction) {
		query.order(property, direction);
		return this;
	}

	public DatastoreQueryTransformer<F, T> sort(String property) {
		sort(property, null);
		return this;
	}

	public DatastoreQueryTransformer<F, T> sort(String property, String direction) {
		query.sort(property, direction);
		return this;
	}

	public DatastoreQueryTransformer<F, T> sort(String entity, String property, String direction) {
		query.sort(entity, property, direction);
		return this;
	}

	public DatastoreQueryTransformer<F, T> limit(int limit) {
		query.limit(limit);
		return this;
	}

	public DatastoreQueryTransformer<F, T> cursor(String cursor) {
		query.cursor(cursor);
		return this;
	}

	public String getCursor() {
		return query.getCursor();
	}

	public DatastoreQueryTransformer<F, T> options(DatastoreQueryOptions options) {
		query.options(options);
		return this;
	}

	public List<T> list() {
		List<T> transformedList = new ArrayList<T>();
		List<F> list = query.executeQueryList();

		for (F object : list) {
			transformedList.add(RepositoryTransformers.<F, T> execute(query.getRepository(), object, transformName));
		}

		query.sortList(transformedList);
		return transformedList;
	}

	public T first() {
		return RepositoryTransformers.execute(query.getRepository(), query.first(), transformName);
	}

	public T only() {
		return RepositoryTransformers.execute(query.getRepository(), query.only(), transformName);
	}

	public T fetch(IdRef<?> idRef) {
		return RepositoryTransformers.execute(query.getRepository(), query.fetch(idRef), transformName);
	}

	public T fetch(Long id) {
		return RepositoryTransformers.execute(query.getRepository(), query.fetch(id), transformName);
	}

	public T fetch(String name) {
		return RepositoryTransformers.execute(query.getRepository(), query.fetch(name), transformName);
	}

}
