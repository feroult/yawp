package endpoint.repository.actions;

import java.util.List;

import endpoint.repository.IdRef;
import endpoint.repository.actions.annotations.PUT;
import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;

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
