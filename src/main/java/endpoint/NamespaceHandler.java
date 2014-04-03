package endpoint;


public interface NamespaceHandler {

	public void set(Class<? extends DatastoreObject> clazz);

	public void reset();

}
