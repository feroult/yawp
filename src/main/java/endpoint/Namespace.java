package endpoint;

import com.google.appengine.api.NamespaceManager;

import endpoint.annotations.Global;

public class Namespace {

	public static String GLOBAL = "";

	protected String ns;

	protected String previousNs;

	public Namespace() {
		this(GLOBAL);
	}

	public Namespace(String ns) {
		this.ns = normalizeNs(ns);
	}

	public void set(Class<?> clazz) {
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
		this.ns = normalizeNs(ns);
	}

	public String getNs() {
		return ns;
	}

	private String normalizeNs(String ns) {
		return ns == null ? GLOBAL : ns;
	}
}
