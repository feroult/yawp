package io.yawp.driver.mock;

import io.yawp.driver.mock.MockOperation.Type;
import io.yawp.repository.IdRef;

import java.util.ArrayList;
import java.util.List;

public class MockTransaction {

	private List<MockOperation> operations = new ArrayList<MockOperation>();

	public void add(Type operationType, IdRef<?> id, Object object, Object previousObject) {
		operations.add(new MockOperation(operationType, id, object, previousObject));
	}

	public void rollback() {
		for (MockOperation operation : operations) {
			operation.rollback();
		}
	};
}
