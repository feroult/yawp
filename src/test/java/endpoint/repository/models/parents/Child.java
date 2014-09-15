package endpoint.repository.models.parents;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Index;
import endpoint.repository.annotations.ParentId;

@Endpoint(path = "/children")
public class Child {

	@Id
	private IdRef<Child> id;

	@ParentId
	private IdRef<Parent> parentId;

	@Index
	private String name;

	public Child() {

	}

	public Child(String name) {
		this.name = name;
	}

	public IdRef<Child> getId() {
		return id;
	}

	public void setId(IdRef<Child> id) {
		this.id = id;
	}

	public IdRef<Parent> getParentId() {
		return parentId;
	}

	public void setParentId(IdRef<Parent> parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
