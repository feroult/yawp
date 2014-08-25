package endpoint;


public class AnotherObjectWithIdRef {

	@Id
	private IdRef<AnotherObjectWithIdRef> id;

	@Parent
	private IdRef<ObjectWithIdRef> objectWithIdRefId;

	private String text;
	
	public AnotherObjectWithIdRef() {
	}

	public AnotherObjectWithIdRef(IdRef<ObjectWithIdRef> objectWithIdRefId, String text) {
		this.objectWithIdRefId = objectWithIdRefId;
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public IdRef<AnotherObjectWithIdRef> getId() {
		return id;
	}

	public void setId(IdRef<AnotherObjectWithIdRef> id) {
		this.id = id;
	}

	public void setObjectWithIdRefId(IdRef<ObjectWithIdRef> objectWithIdRefId) {
		this.objectWithIdRefId = objectWithIdRefId;
	}
	
	public IdRef<ObjectWithIdRef> getObjectWithIdRefId() {
		return objectWithIdRefId;
	}
}
