package endpoint.repository.actions;

import endpoint.repository.IdRef;
import endpoint.repository.ObjectWithIdRef;
import endpoint.repository.actions.annotations.PUT;

public class ObjectWithIdRefAction extends Action<ObjectWithIdRef> {

	@PUT("upper")
	public void upper(IdRef<ObjectWithIdRef> id) {
		ObjectWithIdRef object = id.fetch();
		object.setText(object.getText().toUpperCase());
		r.save(object);
	}
}
