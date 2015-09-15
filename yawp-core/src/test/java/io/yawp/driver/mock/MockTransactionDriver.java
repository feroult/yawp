package io.yawp.driver.mock;

import io.yawp.driver.api.TransactionDriver;

public class MockTransactionDriver implements TransactionDriver {

	private String tx;

	@Override
	public TransactionDriver begin() {
		tx = MockStore.createTransaction();
		return this;
	}

	@Override
	public TransactionDriver beginX() {
		tx = MockStore.createTransaction();
		return this;
	}

	@Override
	public void rollback() {
		MockStore.rollback(tx);

	}

	@Override
	public void commit() {
		MockStore.commit(tx);
	}

	public String getTx() {
		return this.tx;
	}

}
