package endpoint;

import com.google.appengine.api.datastore.Query.SortDirection;

public class DatastoreQueryOrder {

	private String property;

	private String direction;

	public DatastoreQueryOrder(String property, String direction) {
		this.property = property;
		this.direction = direction;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public SortDirection getSortDirection() {
		if (isDesc()) {
			return SortDirection.DESCENDING;
		}
		if (isAsc()) {
			return SortDirection.ASCENDING;
		}
		throw new RuntimeException("invalid sort direction");
	}

	public boolean isAsc() {
		return direction.equalsIgnoreCase("asc");
	}

	public boolean isDesc() {
		return direction.equalsIgnoreCase("desc");
	}

}
