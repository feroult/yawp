package io.yawp.repository.query;

import io.yawp.commons.utils.ReflectionUtils;

import com.google.appengine.api.datastore.Query.SortDirection;

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	int compare(Object o1, Object o2) {
		Comparable value1 = getComparable(o1);
		Comparable value2 = getComparable(o2);

		if (value1 == null) {
			if (value2 == null) {
				return 0;
			} else {
				return isAsc() ? -1 : 1;
			}
		} else if (value2 == null) {
			return isAsc() ? 1 : -1;
		}

		int compare = value1.compareTo(value2);

		if (isDesc()) {
			compare *= -1;
		}
		return compare;
	}

	@SuppressWarnings("rawtypes")
	private Comparable getComparable(Object o) {
		if (entity != null) {
			Object innerObject = ReflectionUtils.getter(o, entity);
			return (Comparable) ReflectionUtils.getter(innerObject, property);
		}
		return (Comparable) ReflectionUtils.getter(o, property);
	}
}
