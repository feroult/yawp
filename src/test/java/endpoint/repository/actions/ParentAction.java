package endpoint.repository.actions;

import java.util.List;
import java.util.Map;

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

	@PUT("touched_with_params")
	public Parent touchWithParams(IdRef<Parent> id, Map<String, String> params) {
		Parent parent = id.fetch();
		parent.setName("touched " + parent.getName() + " " + params.get("x"));
		return parent;
	}

	@PUT(value = "touched_with_params", overCollection = true)
	public List<Parent> touchWithParams(Map<String, String> params) {
		List<Parent> parents = r.query(Parent.class).list();
		for (Parent parent : parents) {
			parent.setName("touched " + parent.getName() + " " + params.get("x"));
		}
		return parents;
	}
}
