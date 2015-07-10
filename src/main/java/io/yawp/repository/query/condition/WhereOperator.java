package io.yawp.repository.query.condition;

import com.google.appengine.api.datastore.Query.FilterOperator;

public enum WhereOperator {

	EQUAL {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.EQUAL;
		}

		@Override
		public WhereOperator reverse() {
			return NOT_EQUAL;
		}
	},
	GREATER_THAN {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.GREATER_THAN;
		}

		@Override
		public WhereOperator reverse() {
			return LESS_THAN_OR_EQUAL;
		}
	},
	GREATER_THAN_OR_EQUAL {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.GREATER_THAN_OR_EQUAL;
		}

		@Override
		public WhereOperator reverse() {
			return LESS_THAN;
		}
	},
	IN {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.IN;
		}

		@Override
		public WhereOperator reverse() {
			throw new RuntimeException("Cannot invert (call not) on IN operators.");
		}
	},
	LESS_THAN {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.LESS_THAN;
		}

		@Override
		public WhereOperator reverse() {
			return GREATER_THAN_OR_EQUAL;
		}
	},
	LESS_THAN_OR_EQUAL {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.LESS_THAN_OR_EQUAL;
		}

		@Override
		public WhereOperator reverse() {
			return GREATER_THAN;
		}
	},
	NOT_EQUAL {
		@Override
		public FilterOperator getFilterOperator() {
			return FilterOperator.NOT_EQUAL;
		}

		@Override
		public WhereOperator reverse() {
			return EQUAL;
		}
	};

	public abstract FilterOperator getFilterOperator();

	public abstract WhereOperator reverse();

	public static WhereOperator toOperator(String operator) {
		if (operator.equals("=")) {
			return EQUAL;
		}
		if (operator.equals(">")) {
			return GREATER_THAN;
		}
		if (operator.equals(">=")) {
			return GREATER_THAN_OR_EQUAL;
		}
		if (operator.equalsIgnoreCase("in")) {
			return IN;
		}
		if (operator.equals("<")) {
			return LESS_THAN;
		}
		if (operator.equals("<=")) {
			return LESS_THAN_OR_EQUAL;
		}
		if (operator.equals("!=")) {
			return NOT_EQUAL;
		}
		throw new RuntimeException("invalid filter operator " + operator);
	}
}
