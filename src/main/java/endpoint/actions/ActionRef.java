package endpoint.actions;

import java.io.Serializable;

import endpoint.routing.HttpVerb;

public class ActionRef implements Serializable {

	private static final long serialVersionUID = -8103211642342487434L;

	private HttpVerb verb;
	private String name;
	private boolean overCollection;

	public ActionRef(HttpVerb verb, String name, boolean overCollection) {
		this.verb = verb;
		this.name = name;
		this.overCollection = overCollection;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (overCollection ? 1231 : 1237);
		result = prime * result + ((verb == null) ? 0 : verb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ActionRef other = (ActionRef) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (overCollection != other.overCollection) {
			return false;
		}
		if (verb != other.verb) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "<" + this.verb + ">" + this.name + (this.overCollection ? "[]" : "");
	}

	public boolean isOverCollection() {
		return this.overCollection;
	}
}
