package endpoint;

public interface Namespace {

	public static String GLOBAL = "";

	public abstract void set(Class<? extends DatastoreObject> clazz);

	public abstract void reset();

	public abstract String get();

}