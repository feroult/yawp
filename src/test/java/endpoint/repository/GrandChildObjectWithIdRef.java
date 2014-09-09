package endpoint.repository;

import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.ParentId;

@Endpoint
public class GrandChildObjectWithIdRef {

	@Id
	private IdRef<GrandChildObjectWithIdRef> id;

	@ParentId
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
