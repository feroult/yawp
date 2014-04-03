package endpoint;

import com.google.appengine.api.NamespaceManager;

public class SimpleNamespaceHandler implements NamespaceHandler {

	private String ns;

	private String previousNs;

	public SimpleNamespaceHandler(String ns) {
		this.ns = ns;
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

}
