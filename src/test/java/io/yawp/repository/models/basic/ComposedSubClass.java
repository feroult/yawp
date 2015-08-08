package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

import org.apache.commons.lang3.StringUtils;

@Endpoint(path = "/composed_subclasses")
public class ComposedSubClass extends ComposedSuperClass {

	@Id
	private IdRef<ComposedSubClass> id;

	public ComposedSubClass() {
		super(StringUtils.EMPTY);
	}

	public ComposedSubClass(String name) {
		super(name);
	}

	public IdRef<ComposedSubClass> getId() {
		return id;
	}

	public void setId(IdRef<ComposedSubClass> id) {
		this.id = id;
	}
}
