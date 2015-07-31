package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.models.basic.facades.ShieldedObjectFacades.AmyFacade;

@Endpoint(path = "/shielded_objects")
public class ShieldedObject implements AmyFacade {

	@Id
	private IdRef<ShieldedObject> id;

	@Index
	private String stringValue;

	private Integer intValue;

	public ShieldedObject() {

	}

	public ShieldedObject(String stringValue) {
		this.stringValue = stringValue;
	}

	public IdRef<ShieldedObject> getId() {
		return id;
	}

	public void setId(IdRef<ShieldedObject> id) {
		this.id = id;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
}
