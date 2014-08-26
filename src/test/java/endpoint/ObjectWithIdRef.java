package endpoint;

import java.util.List;

import endpoint.annotations.Id;
import endpoint.annotations.Index;
import endpoint.annotations.Json;
import endpoint.annotations.Parent;

public class ObjectWithIdRef {

	@Id
	private IdRef<ObjectWithIdRef> id;

	@Parent
	@Index
	private IdRef<AnotherSimpleObject> anotherSimpleObjectId;

	@Json
	private List<IdRef<AnotherSimpleObject>> objectIds;

	private String text;

	public ObjectWithIdRef() {

	}

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

	public IdRef<AnotherSimpleObject> getAnotherSimpleObjectId() {
		return anotherSimpleObjectId;
	}

	public void setAnotherSimpleObjectId(IdRef<AnotherSimpleObject> anotherSimpleObjectId) {
		this.anotherSimpleObjectId = anotherSimpleObjectId;
	}

	public List<IdRef<AnotherSimpleObject>> getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(List<IdRef<AnotherSimpleObject>> objectIds) {
		this.objectIds = objectIds;
	}

}
