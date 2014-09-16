package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.ParentId;

@Endpoint(path = "/grandchildren")
public class Grandchild {

	@Id
	private IdRef<Grandchild> id;

	@ParentId
	private IdRef<Child> childId;

	@Index
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
