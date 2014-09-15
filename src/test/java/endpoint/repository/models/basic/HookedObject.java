package endpoint.repository.models.basic;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Index;

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
