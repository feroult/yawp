package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.JsonUtils;

public class Key {

	private Key parent;

	private String kind;

	private Long id;

	private String name;

	protected Key(String kind) {
		this.kind = kind;
	}

	protected Key(Key parent, String kind, String name) {
		this.parent = parent;
		this.kind = kind;
		this.name = name;
	}

	protected Key(Key parent, String kind, Long id) {
		this.parent = parent;
		this.kind = kind;
		this.id = id;
	}

	protected Key(Key parent, String kind) {
		this.parent = parent;
		this.kind = kind;
	}

	protected Key(String kind, String name) {
		this.kind = kind;
		this.name = name;
	}

	protected Key(String kind, Long id) {
		this.kind = kind;
		this.id = id;
	}

	public Key getParent() {
		return parent;
	}

	public String getKind() {
		return kind;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
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