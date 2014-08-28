package endpoint.actions;

import endpoint.ChildWithIdRef;
import endpoint.IdRef;
import endpoint.ObjectWithIdRef;
import endpoint.actions.annotations.PUT;

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
