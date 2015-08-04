package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

@Endpoint(path = "/deleted_hooked_objects")
public class DeletedHookedObject {

	@Id
	private IdRef<DeletedHookedObject> id;

	@Index
	private String stringValue;

	public DeletedHookedObject() {
		super();
	}

	public DeletedHookedObject(HookedObject object) {
		this.stringValue = object.getStringValue();
	}

	public IdRef<DeletedHookedObject> getId() {
		return id;
	}

	public void setId(IdRef<DeletedHookedObject> id) {
		this.id = id;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

}
