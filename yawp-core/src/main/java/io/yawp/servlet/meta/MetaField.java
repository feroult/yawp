package io.yawp.servlet.meta;

import java.lang.reflect.Field;

import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

public class MetaField {

	private String name;
	private String type;
	private boolean isId;
	private boolean isIndexed;

	public MetaField(Field field) {
		this.name = field.getName();
		this.type = field.getType().getCanonicalName();
		this.isId = field.getAnnotation(Id.class) != null;
		this.isIndexed = field.getAnnotation(Index.class) != null;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isId() {
		return isId;
	}

	public boolean isIndexed() {
		return isIndexed;
	}
}
