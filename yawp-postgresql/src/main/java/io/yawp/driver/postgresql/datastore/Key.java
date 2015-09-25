package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.JsonUtils;

public class Key {

	private Key parentKey;

	private String kind;

	private Long id;

	private String name;

	public Key(String kind) {
		this.kind = kind;
	}

	private Key(Key parentKey, String kind, Long id) {
		this.parentKey = parentKey;
		this.kind = kind;
		this.id = id;
	}

	private Key(Key parentKey, String kind) {
		this.parentKey = parentKey;
		this.kind = kind;
	}

	public Key(String kind, String name) {
		this.kind = kind;
		this.name = name;
	}

	public Key(String kind, Long id) {
		this.kind = kind;
		this.id = id;
	}

	public static Key create(Key parentKey, String kind) {
		return new Key(parentKey, kind);
	}

	public static Key create(String kind) {
		return new Key(kind);
	}

	public static Key create(String kind, String name) {
		return new Key(kind, name);
	}

	public static Key create(String kind, Long id) {
		return new Key(kind, id);
	}

	public static Key create(Key parentKey, String kind, Long id) {
		return new Key(parentKey, kind, id);
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
		this.name = NameGenerator.generate();
	}

}