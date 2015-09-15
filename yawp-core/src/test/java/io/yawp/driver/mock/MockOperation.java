package io.yawp.driver.mock;

import io.yawp.repository.IdRef;

public class MockOperation {

	public enum Type {
		PUT {
			@Override
			public void rollback(IdRef<?> id, Object object, Object previousObject) {
				if (previousObject != null) {
					MockStore.put(id, previousObject, null);
					return;
				}

				MockStore.remove(id, null);
			}
		},
		REMOVE {
			@Override
			public void rollback(IdRef<?> id, Object object, Object previousObject) {
				if (previousObject == null) {
					return;
				}
				MockStore.put(id, previousObject, null);
			}
		};

		public abstract void rollback(IdRef<?> id, Object object, Object previousObject);
	}

	private Type operationType;

	private IdRef<?> id;

	private Object object;

	private Object previousObject;

	public MockOperation(Type operationType, IdRef<?> id, Object object, Object previousObject) {
		this.operationType = operationType;
		this.id = id;
		this.object = object;
		this.previousObject = previousObject;
	}

	public void rollback() {
		operationType.rollback(id, object, previousObject);
	}

}
