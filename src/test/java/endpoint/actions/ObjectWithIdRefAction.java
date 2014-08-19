package endpoint.actions;

import endpoint.IdRef;
import endpoint.ObjectWithIdRef;

public class ObjectWithIdRefAction extends Action<ObjectWithIdRef> {

	@PUT("upper")
	public void upper(IdRef<ObjectWithIdRef> id) {
		ObjectWithIdRef object = id.fetch();
		object.setText(object.getText().toUpperCase());
		r.save(object);
	}
}
