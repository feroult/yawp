package endpoint;

public interface Namespace {

	String GLOBAL = "";

	public abstract void set(Class<? extends DatastoreObject> clazz);

	public abstract void reset();

}