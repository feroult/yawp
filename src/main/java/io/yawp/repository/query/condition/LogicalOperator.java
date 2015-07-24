package io.yawp.repository.query.condition;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

public enum LogicalOperator {
	AND {
		@Override
		public LogicalOperator not() {
			return OR;
		}
	},
	OR {
		@Override
		public LogicalOperator not() {
			return AND;
		}
	};

	public abstract LogicalOperator not();

	public Filter join(BaseCondition... conditions) throws FalsePredicateException {
		return performJoin(this, conditions);
	}

	public static Filter performJoin(LogicalOperator operation, BaseCondition... conditions) throws FalsePredicateException {
		List<Filter> filters = new ArrayList<>();
		for (int i = 0; i < conditions.length; i++) {
			try {
				BaseCondition condition = conditions[i];
				if (condition.hasPostFilter()) {
					continue;
				}

				filters.add(condition.getPredicate());
			} catch (FalsePredicateException ex) {
				if (operation == AND) {
					throw ex;
				}
			}
		}

		if (filters.isEmpty()) {
			throw new FalsePredicateException();
		}

		if (filters.size() == 1) {
			return filters.get(0);
		}

		Filter[] filtersArray = filters.toArray(new Filter[filters.size()]);

		if (operation == AND) {
			return CompositeFilterOperator.and(filtersArray);
		}
		if (operation == OR) {
			return CompositeFilterOperator.or(filtersArray);
		}

		return null;
	}
}