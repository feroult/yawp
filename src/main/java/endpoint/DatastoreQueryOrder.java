package endpoint;

import com.google.appengine.api.datastore.Query.SortDirection;

import endpoint.utils.EntityUtils;

public class DatastoreQueryOrder {

	private String entity;

	private String property;

	private String direction;

	public DatastoreQueryOrder(String entity, String property, String direction) {
		this.entity = entity;
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
		if (direction == null) {
			return true;
		}
		return direction.equalsIgnoreCase("asc");
	}

	public boolean isDesc() {
		if (direction == null) {
			return false;
		}
		return direction.equalsIgnoreCase("desc");
	}

	@SuppressWarnings("rawtypes")
	int compare(Object o1, Object o2) {
		Comparable value1 = getComparable(o1);
		Comparable value2 = getComparable(o2);

		@SuppressWarnings("unchecked")
		int compare = value1.compareTo(value2);
		if (isDesc()) {
			compare *= -1;
		}
		return compare;
	}

	@SuppressWarnings("rawtypes")
	private Comparable getComparable(Object o) {
		if (entity != null) {
			Object innerObject = EntityUtils.getter(o, entity);
			return (Comparable) EntityUtils.getter(innerObject, property);
		}
		return (Comparable) EntityUtils.getter(o, property);
	}
}
