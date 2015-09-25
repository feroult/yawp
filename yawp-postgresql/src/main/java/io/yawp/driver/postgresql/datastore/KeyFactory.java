package io.yawp.driver.postgresql.datastore;

public class KeyFactory {
	public static Key createKey(Key parent, String kind) {
		return new Key(parent, kind);
	}

	public static Key createKey(String kind) {
		return new Key(kind);
	}

	public static Key createKey(String kind, String name) {
		return new Key(kind, name);
	}

	public static Key createKey(String kind, Long id) {
		return new Key(kind, id);
	}

	public static Key createKey(Key parent, String kind, String name) {
		return new Key(parent, kind, name);
	}

	public static Key createKey(Key parent, String kind, Long id) {
		return new Key(parent, kind, id);
	}

}
