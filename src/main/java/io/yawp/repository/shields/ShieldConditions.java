package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.and;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.List;

public class ShieldConditions {

	private BaseCondition c;

	private Class<?> endpointClazz;

	private IdRef<?> id;

	private List<?> objects;

	private BaseCondition parentC;

	public ShieldConditions(Class<?> endpointClazz, IdRef<?> id, List<?> objects) {
		this.endpointClazz = endpointClazz;
		this.id = id;
		this.objects = objects;
	}

	public void where(BaseCondition c) {
		if (this.c != null) {
			this.c = and(this.c, c);
			return;
		}
		this.c = c;
	}

	public void parentWhere(BaseCondition parentC) {
		this.parentC = parentC;
	}

	public boolean evaluate() {
		return evaluateIncoming() && evaluateExisting() && evaluateParent();
	}

	private boolean evaluateIncoming() {
		if (c == null) {
			return true;
		}

		if (objects == null) {
			return true;
		}
		return evaluateObjects(c, new EvaluateIncoming());
	}

	private boolean evaluateExisting() {
		if (c == null) {
			return true;
		}

		if (objects == null) {
			if (!endpointClazz.equals(id.getClazz())) {
				return true;
			}
			return c.evaluate(id.fetch());
		}
		return evaluateObjects(c, new EvaluateExisting());
	}

	private boolean evaluateParent() {
		if (parentC == null) {
			return true;
		}

		if (objects == null) {
			if (endpointClazz.equals(id.getClazz())) {
				return id.getParentId() == null || parentC.evaluate(id.getParentId().fetch());
			}
			return parentC.evaluate(id.fetch());
		}

		return evaluateObjects(parentC, new EvaluateParent());
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
		public boolean evaluate(BaseCondition c, Object object);
	}

	private class EvaluateIncoming implements Evaluate {
		@Override
		public boolean evaluate(BaseCondition c, Object object) {
			return c.evaluate(object);
		}
	}

	private class EvaluateExisting implements Evaluate {
		@Override
		public boolean evaluate(BaseCondition c, Object object) {
			IdRef<?> id = EntityUtils.getIdRef(object);
			if (id == null) {
				return true;
			}
			return c.evaluate(id.fetch());
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
