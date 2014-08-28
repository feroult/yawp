package endpoint;

import endpoint.annotations.Endpoint;
import endpoint.annotations.Id;

@Endpoint(path = "child")
public class ChildWithIdRef {

	@Id
	private IdRef<ObjectWithIdRef> objectWithIdRefId;

	private String text;

	public ChildWithIdRef() {
	}

	public ChildWithIdRef(String text) {
		this.text = text;
	}

	public IdRef<ObjectWithIdRef> getObjectWithIdRefId() {
		return objectWithIdRefId;
	}

	public void setObjectWithIdRefId(IdRef<ObjectWithIdRef> objectWithIdRefId) {
		this.objectWithIdRefId = objectWithIdRefId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
