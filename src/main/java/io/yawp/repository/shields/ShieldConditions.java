package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.and;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShieldConditions {

	private BaseCondition condition;

	private Map<Integer, BaseCondition> ancestorConditions = new HashMap<Integer, BaseCondition>();

	private BaseCondition parentCondition;

	private Class<?> endpointClazz;

	private IdRef<?> id;

	private List<?> objects;

	public ShieldConditions(Class<?> endpointClazz, IdRef<?> id, List<?> objects) {
		this.endpointClazz = endpointClazz;
		this.id = id;
		this.objects = objects;
	}

	public void where(BaseCondition condition) {
		if (this.condition != null) {
			this.condition = and(this.condition, condition);
			return;
		}
		this.condition = condition;
	}

	public void whereParent(BaseCondition parentCondition) {
		whereAncestor(0, parentCondition);
		this.parentCondition = parentCondition;
	}

	public void whereGrandparent(BaseCondition grandparentC) {
		// TODO Auto-generated method stub
	}

	public void whereAncestor(int i, BaseCondition condition) {
		if (ancestorConditions.containsKey(i)) {
			ancestorConditions.put(i, and(ancestorConditions.get(i), condition));
			return;
		}
		ancestorConditions.put(i, condition);
	}

	public boolean evaluate() {
		return evaluateIncoming() && evaluateExisting() && evaluateAncestors();
	}

	private boolean evaluateIncoming() {
		if (condition == null) {
			return true;
		}

		if (objects == null) {
			return true;
		}

		return evaluateObjects(condition, new EvaluateIncoming());
	}

	private boolean evaluateExisting() {
		if (condition == null) {
			return true;
		}

		if (objects == null) {
			if (!endpointClazz.equals(id.getClazz())) {
				return true;
			}
			return condition.evaluate(id.fetch());
		}

		return evaluateObjects(condition, new EvaluateExisting());
	}

	private boolean evaluateAncestors() {
		if (ancestorConditions.size() == 0) {
			return true;
		}

		if (objects == null) {
			return evaluateAncestorByIds();
		}

		return evaluateObjects(parentCondition, new EvaluateParent());
	}

	private boolean evaluateAncestorByIds() {
		boolean result = true;
		for (Integer ancestor : sortAncestorIndexes()) {
			BaseCondition ancestorCondition = ancestorConditions.get(ancestor);
			IdRef<?> ancestorId = getAncestorId(ancestor, id);
			result = result && (ancestorId == null || ancestorCondition.evaluate(ancestorId.fetch()));
			if (!result) {
				return false;
			}
		}
		return true;
	}

	private IdRef<?> getAncestorId(int ancestor, IdRef<?> id) {
		Class<?> ancestorClazz = EntityUtils.getAncestorClazz(ancestor, endpointClazz);
		IdRef<?> ancestorId = id;

		for (int i = 0; i <= ancestor; i++) {
			if (ancestorId.getClazz().equals(ancestorClazz)) {
				return ancestorId;
			}
			if (ancestorId.getParentId() == null) {
				return null;
			}
			ancestorId = ancestorId.getParentId();
		}
		return null;
	}

	private List<Integer> sortAncestorIndexes() {
		ArrayList<Integer> conditionsIndex = new ArrayList<Integer>(ancestorConditions.keySet());
		Collections.sort(conditionsIndex);
		return conditionsIndex;
	}

	private boolean evaluateObjects(BaseCondition condition, Evaluate e) {
		boolean result = true;
		for (Object object : objects) {
			result = result && e.evaluate(condition, object);
			if (!result) {
				return false;
			}
		}
		return true;
	}

	private interface Evaluate {
		public boolean evaluate(BaseCondition condition, Object object);
	}

	private class EvaluateIncoming implements Evaluate {
		@Override
		public boolean evaluate(BaseCondition condition, Object object) {
			return condition.evaluate(object);
		}
	}

	private class EvaluateExisting implements Evaluate {
		@Override
		public boolean evaluate(BaseCondition condition, Object object) {
			IdRef<?> id = EntityUtils.getIdRef(object);
			if (id == null) {
				return true;
			}
			return condition.evaluate(id.fetch());
		}
	}

	private class EvaluateParent implements Evaluate {
		@Override
		public boolean evaluate(BaseCondition c, Object object) {
			IdRef<?> id = EntityUtils.getParentIdRef(object);
			if (id == null) {
				return true;
			}
			return c.evaluate(id.fetch());
		}
	}

}
