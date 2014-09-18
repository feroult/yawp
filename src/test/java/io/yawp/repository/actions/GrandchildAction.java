package io.yawp.repository.actions;

import io.yawp.repository.IdRef;
import io.yawp.repository.actions.annotations.PUT;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;

import java.util.List;

public class GrandchildAction extends Action<Grandchild> {

	@PUT("touched")
	public Grandchild touchObject(IdRef<Grandchild> id) {
		Grandchild grandchild = id.fetch();
		grandchild.setName("touched " + grandchild.getName());
		return grandchild;
	}

	@PUT(value = "touched", overCollection = true)
	public List<Grandchild> touchCollection(IdRef<Child> childId) {
		List<Grandchild> grandchilds = r.query(Grandchild.class).from(childId).list();
		for (Grandchild grandchild : grandchilds) {
			grandchild.setName("touched " + grandchild.getName());
		}
		return grandchilds;
	}

}
