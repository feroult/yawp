package endpoint;

import com.google.appengine.api.NamespaceManager;

public class BasicNamespace implements Namespace {

	protected String ns;

	protected String previousNs;

	public BasicNamespace() {
		this(Namespace.GLOBAL);
	}

	public BasicNamespace(String ns) {
		this.ns = ns == null ? Namespace.GLOBAL : ns;
	}

	@Override
	public void set(Class<? extends DatastoreObject> clazz) {
		previousNs = NamespaceManager.get();
		NamespaceManager.set(ns);
	}

	@Override
	public void reset() {
		NamespaceManager.set(previousNs);
	}

	@Override
	public String get() {
		return ns;
	}

}
