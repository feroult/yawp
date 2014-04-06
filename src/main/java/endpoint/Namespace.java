package endpoint;

import com.google.appengine.api.NamespaceManager;

public class Namespace {

	public static String GLOBAL = "";

	protected String ns;

	protected String previousNs;

	public Namespace() {
		this(GLOBAL);
	}

	public Namespace(String ns) {
		this.ns = ns == null ? GLOBAL : ns;
	}

	public void set(Class<? extends DatastoreObject> clazz) {
		previousNs = NamespaceManager.get();
		if (clazz.isAnnotationPresent(Global.class)) {
			NamespaceManager.set(GLOBAL);
		} else {
			NamespaceManager.set(ns);
		}
	}

	public void reset() {
		NamespaceManager.set(previousNs);
	}

	public void setNs(String ns) {
		this.ns = ns;		
	}

	public String getNs() {
		return ns;
	}

}
