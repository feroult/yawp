package endpoint.repository.actions;

import java.util.List;

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

	@PUT(value = "touched", overCollection = true)
	public List<Parent> touch() {
		List<Parent> parents = r.query(Parent.class).list();
		for (Parent parent : parents) {
			parent.setName("touched " + parent.getName());
		}
		return parents;
	}

}
