package endpoint.repository.actions;

import endpoint.utils.HttpVerb;

public class ActionKey {

	private HttpVerb verb;

	private String actionName;

	private boolean overCollection;

	public ActionKey(HttpVerb verb, String actionName, boolean overCollection) {
		this.verb = verb;
		this.actionName = actionName;
		this.overCollection = overCollection;
	}

	public boolean isOverCollection() {
		return this.overCollection;
	}

	public String getActionName() {
		return actionName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionName == null) ? 0 : actionName.hashCode());
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
		ActionKey other = (ActionKey) obj;
		if (actionName == null) {
			if (other.actionName != null) {
				return false;
			}
		} else if (!actionName.equals(other.actionName)) {
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
		return "<" + this.verb + ">" + this.actionName + (this.overCollection ? "[]" : "");
	}
}
