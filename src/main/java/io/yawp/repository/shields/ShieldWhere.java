package io.yawp.repository.shields;

import static io.yawp.repository.query.condition.Condition.and;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.condition.BaseCondition;

import java.util.List;

public class ShieldWhere {

	private BaseCondition c;

	private IdRef<?> id;

	private List<?> objects;

	public ShieldWhere(IdRef<?> id, List<?> objects) {
		this.id = id;
		this.objects = objects;
	}

	public void condition(BaseCondition c) {
		if (this.c != null) {
			this.c = and(this.c, c);
			return;
		}
		this.c = c;
	}

	public boolean evaluateIncoming() {
		return evaluateObjects(new EvaluateIncoming());
	}

	public boolean evaluateExisting() {
		if (objects == null) {
			return c.evaluate(id.fetch());
		}
		return evaluateObjects(new EvaluateExisting());
	}

	private boolean evaluateObjects(Evaluate e) {
		boolean result = true;
		for (Object object : objects) {
			result = result && e.evaluate(c, object);
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
			return c.evaluate(id.fetch());
		}
	}

}
