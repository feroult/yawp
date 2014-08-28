package endpoint;

import endpoint.annotations.Endpoint;
import endpoint.annotations.Id;
import endpoint.annotations.Parent;

@Endpoint
public class GrandChildObjectWithIdRef {

	@Id
	private IdRef<GrandChildObjectWithIdRef> id;

	@Parent
	private IdRef<AnotherObjectWithIdRef> anotherObjectWithIdRefId;

	private String text;
	
	public GrandChildObjectWithIdRef() {
	}

	public GrandChildObjectWithIdRef(IdRef<AnotherObjectWithIdRef> anotherObjectWithIdRefId, String text) {
		this.anotherObjectWithIdRefId = anotherObjectWithIdRefId;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public IdRef<GrandChildObjectWithIdRef> getId() {
		return id;
	}

	public void setId(IdRef<GrandChildObjectWithIdRef> id) {
		this.id = id;
	}

	public void setObjectWithIdRefId(IdRef<AnotherObjectWithIdRef> anotherObjectWithIdRefId) {
		this.anotherObjectWithIdRefId = anotherObjectWithIdRefId;
	}
	
	public IdRef<AnotherObjectWithIdRef> getAnotherObjectWithIdRefId() {
		return anotherObjectWithIdRefId;
	}
}
