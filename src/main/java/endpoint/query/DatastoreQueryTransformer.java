package endpoint.query;

import java.util.ArrayList;
import java.util.List;

import endpoint.IdRef;
import endpoint.transformers.RepositoryTransformers;

// TODO inherits from DatastoreQuery
public class DatastoreQueryTransformer<F, T> {

	private DatastoreQuery<F> query;

	private String transformName;

	public DatastoreQueryTransformer(DatastoreQuery<F> query, String transformName) {
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

	@Deprecated
	public DatastoreQueryTransformer<F, T> whereById(String operator, Long id) {
		query.whereById(operator, id);
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
		List<F> list = query.unsortedList();

		for (F object : list) {
			transformedList.add(RepositoryTransformers.<F, T>execute(query.getRepository(), object, transformName));
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

	@Deprecated
	public T id(Long id) {
		return RepositoryTransformers.execute(query.getRepository(), query.id(id), transformName);
	}

	public T id(IdRef<?> idRef) {
		return id(idRef.asLong());
	}
}
