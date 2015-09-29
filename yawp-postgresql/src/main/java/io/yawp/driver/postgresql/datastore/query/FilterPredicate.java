package io.yawp.driver.postgresql.datastore.query;

public class FilterPredicate extends Filter {

	private String field;

	private FilterOperator filterOperator;

	private Object value;

	public FilterPredicate(String field, FilterOperator filterOperator, Object value) {
		this.field = field;
		this.filterOperator = filterOperator;
		this.value = value;
	}

	@Override
	public String getWhereCaluse() {
		return String.format("%s %s :%s", field, filterOperator, field);
	}

}
