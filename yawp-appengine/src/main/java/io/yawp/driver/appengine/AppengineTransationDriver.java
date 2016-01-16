package io.yawp.driver.appengine;

import io.yawp.driver.api.TransactionDriver;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

public class AppengineTransationDriver implements TransactionDriver {

    private Transaction tx;

    private AppengineEnvironmentDriver environment;

    public AppengineTransationDriver(AppengineEnvironmentDriver environment) {
        this.environment = environment;
    }

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
        if (!environment.isProduction()) {
            return this;
        }

        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        tx = datastore().beginTransaction(options);
        return this;
    }

    @Override
    public void rollback() {
        if (tx == null) {
            return;
        }

        if (!tx.isActive()) {
            tx = null;
            return;
        }

        tx.rollback();
        tx = null;
    }

    @Override
    public void commit() {
        if (tx == null) {
            return;
        }

        tx.commit();
        tx = null;
    }

}
