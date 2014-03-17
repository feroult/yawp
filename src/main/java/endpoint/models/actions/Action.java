package endpoint.models.actions;

import endpoint.models.Repository;

// TODO move actions to controller layer
public class Action {

	protected Repository r;

	public void setRepository(Repository r) {
		this.r = r;
	}

}
