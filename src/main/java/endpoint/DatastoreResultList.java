package endpoint;

import java.util.List;

public class DatastoreResultList<T> {

	private List<T> objects;

	public DatastoreResultList() {
	}

	public DatastoreResultList(List<T> objects) {
		this.objects = objects;
	}

	public List<T> now() {
		return objects;
	}

}
