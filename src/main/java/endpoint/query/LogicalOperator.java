package endpoint.query;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import java.util.ArrayList;
import java.util.List;

public enum LogicalOperator {
	AND, OR;

	public Filter join(Class<?> clazz, Condition... conditions) throws FalsePredicateException {
		return performJoin(this, clazz, conditions);
	}
	
	public static Filter performJoin(LogicalOperator operation, Class<?> clazz, Condition... conditions) throws FalsePredicateException {
		List<Filter> filters = new ArrayList<>();
		for (int i = 0; i < conditions.length; i++) {
			try {
				filters.add(conditions[i].getPredicate(clazz));
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