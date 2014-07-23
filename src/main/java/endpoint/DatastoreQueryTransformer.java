package endpoint;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

import endpoint.transformers.RepositoryTransformers;

public class DatastoreQueryTransformer<T> {

	private DatastoreQuery<?> query;

	private Class<T> transformClazz;

	private String transformName;

	public DatastoreQueryTransformer(DatastoreQuery<?> query, Class<T> transformClazz, String transformName) {
		this.query = query;
		this.transformClazz = transformClazz;
		this.transformName = transformName;
	}

	public DatastoreQueryTransformer<T> where(Object... values) {
		query.where(values);
		return this;
	}

	public DatastoreQueryTransformer<T> parent(Key parentKey) {
		query.parent(parentKey);
		return this;
	}

	public DatastoreQueryTransformer<T> order(String property) {
		order(property, null);
		return this;
	}

	public DatastoreQueryTransformer<T> order(String property, String direction) {
		query.order(property, direction);
		return this;
	}

	public DatastoreQueryTransformer<T> sort(String property) {
		sort(property, null);
		return this;
	}

	public DatastoreQueryTransformer<T> sort(String property, String direction) {
		query.sort(property, direction);
		return this;
	}

	public DatastoreQueryTransformer<T> limit(int limit) {
		query.limit(limit);
		return this;
	}

	public DatastoreQueryTransformer<T> cursor(String cursor) {
		query.cursor(cursor);
		return this;
	}

	public String getCursor() {
		return query.getCursor();
	}

	public DatastoreQueryTransformer<T> options(DatastoreQueryOptions options) {
		query.options(options);
		return this;
	}

	public List<T> list() {
		List<T> transformedList = new ArrayList<T>();
		List<?> list = query.unsortedList();

		for (Object object : list) {
			transformedList.add(RepositoryTransformers.execute(query.getRepository(), transformClazz, object, transformName));
		}

		query.sortList(transformedList);
		return transformedList;
	}

	public T first() {
		return RepositoryTransformers.execute(query.getRepository(), transformClazz, query.first(), transformName);
	}

	public T id(Long id) {
		return RepositoryTransformers.execute(query.getRepository(), transformClazz, query.id(id), transformName);
	}

}
