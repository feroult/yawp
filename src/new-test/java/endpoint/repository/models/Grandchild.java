package endpoint.repository.models;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.ParentId;

@Endpoint(path = "/grandchildren")
public class Grandchild {

	@Id
	private IdRef<Grandchild> id;

	@ParentId
	private IdRef<Child> childId;

	private String name;

	public Grandchild() {

	}

	public Grandchild(String name) {
		this.name = name;
	}

	public IdRef<Grandchild> getId() {
		return id;
	}

	public void setId(IdRef<Grandchild> id) {
		this.id = id;
	}

	public IdRef<Child> getChildId() {
		return childId;
	}

	public void setChildId(IdRef<Child> childId) {
		this.childId = childId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
