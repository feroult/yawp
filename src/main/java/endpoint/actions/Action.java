package endpoint.actions;

import endpoint.Repository;

// TODO move actions to controller layer
public class Action {

	protected Repository r;

	public void setRepository(Repository r) {
		this.r = r;
	}

}
