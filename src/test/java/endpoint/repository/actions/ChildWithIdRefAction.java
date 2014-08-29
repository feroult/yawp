package endpoint.repository.actions;

import endpoint.repository.ChildWithIdRef;
import endpoint.repository.IdRef;
import endpoint.repository.ObjectWithIdRef;
import endpoint.repository.actions.annotations.PUT;

public class ChildWithIdRefAction extends Action<ChildWithIdRef> {

	@PUT("lower")
	public void lower(IdRef<ChildWithIdRef> id) {
		ObjectWithIdRef object = id.fetch(ObjectWithIdRef.class);
		object.setText(object.getText().toLowerCase());
		r.save(object);

		ChildWithIdRef child = id.fetch(ChildWithIdRef.class);
		child.setText(child.getText().toLowerCase());
		r.save(child);
	}
}
