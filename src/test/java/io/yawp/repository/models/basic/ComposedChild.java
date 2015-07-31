package io.yawp.repository.models.basic;

import org.apache.commons.lang3.StringUtils;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/composed_children")
public class ComposedChild extends ComposedParent {

	@Id
	private IdRef<ComposedChild> id;

	public ComposedChild() {
		super(StringUtils.EMPTY);
	}

	public ComposedChild(String name) {
		super(name);
	}

	public IdRef<ComposedChild> getId() {
		return id;
	}

	public void setId(IdRef<ComposedChild> id) {
		this.id = id;
	}
}
