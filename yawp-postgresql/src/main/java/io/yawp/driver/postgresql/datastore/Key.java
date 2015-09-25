package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.JsonUtils;

import java.util.UUID;

public class Key {

	private String kind;

	private Key parentKey;

	private Long id;

	private String name;

	public Key(String kind) {
		this.kind = kind;
	}

	private Key(String kind, Key parentKey) {
		this.kind = kind;
		this.parentKey = parentKey;
	}

	public Key(String kind, String name) {
		this.kind = kind;
		this.name = name;
	}

	public static Key create(String kind, Key parentKey) {
		return new Key(kind, parentKey);
	}

	public static Key create(String kind) {
		return new Key(kind);
	}

	public static Key create(String kind, String name) {
		return new Key(kind, name);
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

	public static Key deserialize(String json) {
		return JsonUtils.from(null, json, Key.class);
	}

	public boolean isNew() {
		return name == null && id == null;
	}

	public void generate() {
		this.name = nextName();
	}

	private String nextName() {
		return UUID.randomUUID().toString();
	}

}