package io.yawp.repository.driver.appengine;

import io.yawp.repository.driver.api.TransactionDriver;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

public class AppengineTransationDriver implements TransactionDriver {

	private Transaction tx;

	private DatastoreService datastore() {
		return DatastoreServiceFactory.getDatastoreService();
	}

	@Override
	public TransactionDriver begin() {
		tx = datastore().beginTransaction();
		return this;
	}

	@Override
	public TransactionDriver beginX() {
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		tx = datastore().beginTransaction(options);
		return this;
	}

	@Override
	public void rollback() {
		if (!tx.isActive()) {
			tx = null;
			return;
		}

		tx.rollback();
		tx = null;
	}

	@Override
	public void commit() {
		tx.commit();
		tx = null;
	}

}
