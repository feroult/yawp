package endpoint;

import java.util.List;

import com.google.appengine.api.datastore.Key;

import endpoint.response.HttpResponse;

public class NamespaceRepository extends Repository {

	private NamespaceHandler namespace;

	public NamespaceRepository(NamespaceHandler namespace) {
		this.namespace = namespace;
	}

	@Override
	public void save(DatastoreObject object) {
		namespace.set(object.getClass());
		try {
			super.save(object);
		} finally {
			namespace.reset();
		}
	}

	@Override
	public HttpResponse action(Class<? extends DatastoreObject> clazz, String method, String action, long id) {
		namespace.set(clazz);
		try {
			return super.action(clazz, method, action, id);
		} finally {
			namespace.reset();
		}

	}

	@Override
	public <T extends DatastoreObject> List<T> all(Class<T> clazz) {
		namespace.set(clazz);
		try {
			return query(clazz).asList();
		} finally {
			namespace.reset();
		}

	}

	@Override
	public <T extends DatastoreObject> T findByKey(Key key, Class<T> clazz) {
		namespace.set(clazz);
		try {
			return super.findByKey(key, clazz);
		} finally {
			namespace.reset();
		}

	}

	@Override
	public <T extends DatastoreObject> T findById(long id, Class<T> clazz) {
		namespace.set(clazz);
		try {
			return super.findById(id, clazz);
		} finally {
			namespace.reset();
		}

	}

	@Override
	public <T extends DatastoreObject> DatastoreQuery<T> query(Class<T> clazz) {
		namespace.set(clazz);
		try {
			return new NamespaceDatastoreQuery<T>(clazz, namespace);
		} finally {
			namespace.reset();
		}

	}
}
