package io.yawp.driver.postgresql;

import io.yawp.commons.utils.JsonUtils;

import java.util.UUID;

public class Key {

	private String kind;

	private Key parentKey;

	private String name;

	public Key(String kind) {
		this.kind = kind;
		this.name = nextName();
	}

	private Key(String kind, Key parentKey) {
		this.kind = kind;
		this.parentKey = parentKey;
		this.name = nextName();
	}

	public static Key create(String kind, Key parentKey) {
		return new Key(kind, parentKey);
	}

	public static Key create(String kind) {
		return new Key(kind);
	}

	public String getKind() {
		return kind;
	}

	public String getName() {
		return name;
	}

	public String serialize() {
		return JsonUtils.to(this);
	}

	private String nextName() {
		return UUID.randomUUID().toString();
	}
}