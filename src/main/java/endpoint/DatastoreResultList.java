package endpoint;

import java.util.ArrayList;
import java.util.List;

import endpoint.transformers.RepositoryTransformers;

public class DatastoreResultList<T> {

	private Repository r;

	private List<T> objects;

	public DatastoreResultList(Repository r) {
		this.r = r;
	}

	public DatastoreResultList(Repository r, List<T> objects) {
		this.r = r;
		this.objects = objects;
	}

	public List<T> now() {
		return objects;
	}

	public DatastoreResultList<?> transform(String name) {
		return transform(Object.class, name);
	}

	public <TT> DatastoreResultList<TT> transform(Class<TT> clazz, String name) {
		List<TT> transformedList = new ArrayList<TT>();

		for (T object : objects) {
			transformedList.add(RepositoryTransformers.execute(r, clazz, object, name));
		}
		return new DatastoreResultList<TT>(r, transformedList);
	}
}
