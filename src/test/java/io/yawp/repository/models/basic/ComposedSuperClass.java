package io.yawp.repository.models.basic;

import io.yawp.repository.annotations.Index;

public class ComposedSuperClass {

	@Index
	protected String name;

	public ComposedSuperClass(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
