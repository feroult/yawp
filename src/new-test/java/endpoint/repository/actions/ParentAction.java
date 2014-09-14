package endpoint.repository.actions;

import endpoint.repository.IdRef;
import endpoint.repository.actions.annotations.PUT;
import endpoint.repository.models.parents.Parent;

public class ParentAction extends Action<Parent> {

	@PUT("touched")
	public Parent touch(IdRef<Parent> id) {
		Parent parent = id.fetch();
		parent.setName("touched " + parent.getName());
		return parent;
	}

}
