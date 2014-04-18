package endpoint;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatastoreObject other = (DatastoreObject) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
