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
		Object transformedObject = RepositoryTransformers.execute(r, object, name);
		return new DatastoreResult<Object>(r, transformedObject);
	}

}
