package endpoint.actions;

import endpoint.IdRef;
import endpoint.ObjectWithIdRef;
import endpoint.Target;

@Target(ObjectWithIdRef.class)
public class ObjectWithIdRefAction extends Action {


	@PUT("upper")
	public void upper(IdRef<ObjectWithIdRef> id) {
		ObjectWithIdRef object = id.fetch();
		object.setText(object.getText().toUpperCase());
		r.save(object);
	}
}
