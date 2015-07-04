package io.yawp.repository.actions;

import io.yawp.repository.IdRef;
import io.yawp.repository.actions.annotations.PUT;
import io.yawp.repository.models.basic.ShieldedObject;

public class ShieldedObjectAction extends Action<ShieldedObject> {

	@PUT("something")
	public String something(IdRef<ShieldedObject> id) {
		return "x";
	}

	@PUT("anotherthing")
	public String anotherthing(IdRef<ShieldedObject> id) {
		return "y";
	}

}
