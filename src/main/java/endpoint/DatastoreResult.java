package endpoint;

import endpoint.transformers.RepositoryTransformers;

public class DatastoreResult<T> {

	private Repository r;

	private T object;

	public DatastoreResult(Repository r) {
		this.r = r;
	}

	public DatastoreResult(Repository r, T object) {
		this.object = object;
		this.r = r;
	}

	public T now() {
		return object;
	}

	public DatastoreResult<?> transform(String name) {
		return transform(Object.class, name);
	}

	public <TT> DatastoreResult<TT> transform(Class<TT> clazz, String name) {
		TT transformedObject = RepositoryTransformers.execute(r, clazz, object, name);
		return new DatastoreResult<TT>(r, transformedObject);
	}

}
