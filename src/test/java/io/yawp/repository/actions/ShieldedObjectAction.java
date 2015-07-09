package io.yawp.repository.actions;

import io.yawp.commons.http.annotation.GET;
import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObject;

public class ShieldedObjectAction extends Action<ShieldedObject> {

	@PUT("something")
	public void something(IdRef<ShieldedObject> id) {
	}

	@PUT("anotherthing")
	public void anotherthing(IdRef<ShieldedObject> id) {
	}

	@GET("collection")
	public void collection() {
	}

}
