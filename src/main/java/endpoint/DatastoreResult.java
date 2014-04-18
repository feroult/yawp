package endpoint;

public class DatastoreResult<T> {

	private T object;

	public DatastoreResult() {
	}

	public DatastoreResult(T object) {
		this.object = object;
	}

	public T now() {
		return object;
	}

	public DatastoreResult<T> transform(String name) {
		// TODO Auto-generated method stub
		return this;
	}

}
