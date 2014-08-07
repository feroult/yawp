package endpoint;

public class ObjectWithIdRef {

	@Id
	private IdRef<ObjectWithIdRef> id;

	private String text;

	public ObjectWithIdRef(String text) {
		this.text = text;
	}

	public IdRef<ObjectWithIdRef> getId() {
		return id;
	}

	public void setId(IdRef<ObjectWithIdRef> id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
