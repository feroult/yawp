package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

@Endpoint(path = "/hooked_objects")
public class HookedObject {

	@Id
	private IdRef<HookedObject> id;

	@Index
	private String stringValue;

	public HookedObject() {

	}

	public HookedObject(String stringValue) {
		this.stringValue = stringValue;
	}

	public IdRef<HookedObject> getId() {
		return id;
	}

	public void setId(IdRef<HookedObject> id) {
		this.id = id;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

}
