package io.yawp.repository.query.condition;

import java.util.Collection;

public enum WhereOperator {

	EQUAL {
		@Override
		public WhereOperator reverse() {
			return NOT_EQUAL;
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			return compareObjects(objectValue, whereValue) == 0;
		}
	},
	GREATER_THAN {
		@Override
		public WhereOperator reverse() {
			return LESS_THAN_OR_EQUAL;
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			return compareObjects(objectValue, whereValue) > 0;
		}

	},
	GREATER_THAN_OR_EQUAL {
		@Override
		public WhereOperator reverse() {
			return LESS_THAN;
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			return compareObjects(objectValue, whereValue) >= 0;
		}
	},
	LESS_THAN {
		@Override
		public WhereOperator reverse() {
			return GREATER_THAN_OR_EQUAL;
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			return compareObjects(objectValue, whereValue) < 0;
		}
	},
	LESS_THAN_OR_EQUAL {
		@Override
		public WhereOperator reverse() {
			return GREATER_THAN;
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			return compareObjects(objectValue, whereValue) <= 0;
		}
	},
	NOT_EQUAL {
		@Override
		public WhereOperator reverse() {
			return EQUAL;
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			return compareObjects(objectValue, whereValue) != 0;
		}
	},
	IN {
		@Override
		public WhereOperator reverse() {
			throw new RuntimeException("Cannot invert (call not) on IN operators.");
		}

		@Override
		public boolean evaluate(Object objectValue, Object whereValue) {
			Collection<?> collection = (Collection<?>) whereValue;
			return collection.contains(objectValue);
		}
	};

	public abstract WhereOperator reverse();

	public abstract boolean evaluate(Object objectValue, Object whereValue);

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static int compareObjects(Object o1, Object o2) {
		if (o1 == null && o2 != null) {
			return -1;
		}

		if (o1 != null && o2 == null) {
			return 1;
		}

		Comparable c1 = (Comparable) o1;
		Comparable c2 = (Comparable) o2;
		return c1.compareTo(c2);
	}

}
