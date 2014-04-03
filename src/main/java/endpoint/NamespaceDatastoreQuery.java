package endpoint;

import java.util.List;

public class NamespaceDatastoreQuery<T extends DatastoreObject> extends DatastoreQuery<T> {

	private NamespaceHandler namespace;

	public NamespaceDatastoreQuery(Class<T> clazz, NamespaceHandler namespace) {
		super(clazz);
		this.namespace = namespace;
	}

	@Override
	public List<T> asList() {
		namespace.set(getClazz());
		try {
			return super.asList();
		} finally {
			namespace.reset();
		}
	}

	@Override
	public T first() {
		namespace.set(getClazz());
		try {
			return super.first();
		} finally {
			namespace.reset();
		}
	}

}
