package io.yawp.repository.actions;

import io.yawp.repository.IdRef;
import io.yawp.repository.actions.annotations.PUT;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;

import java.util.List;

public class ChildAction extends Action<Child> {

	@PUT("touched")
	public Child touchObject(IdRef<Child> id) {
		Child child = id.fetch();
		child.setName("touched " + child.getName());
		return child;
	}

	@PUT(value = "touched", overCollection = true)
	public List<Child> touchCollection(IdRef<Parent> parentId) {
		List<Child> childs = r.query(Child.class).from(parentId).order("name").list();
		for (Child child : childs) {
			child.setName("touched " + child.getName());
		}
		return childs;
	}

}
