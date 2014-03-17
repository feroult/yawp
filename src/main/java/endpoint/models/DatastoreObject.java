package endpoint.models;

import com.google.appengine.api.datastore.Key;

import endpoint.utils.JsonUtils;

public class DatastoreObject {

	private Key key;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Long getId() {
		if (key == null) {
			return null;
		}
		return key.getId();
	}

	public String getJson() {
		return JsonUtils.to(this);
	}
}
