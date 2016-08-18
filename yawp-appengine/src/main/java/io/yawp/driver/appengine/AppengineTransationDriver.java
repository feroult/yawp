package io.yawp.driver.appengine;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import io.yawp.driver.api.TransactionDriver;

import java.util.logging.Logger;

public class AppengineTransationDriver implements TransactionDriver {

    private final static Logger logger = Logger.getLogger(AppengineTransationDriver.class.getName());

    private Transaction tx;

    private AppengineEnvironmentDriver environment;

    private DatastoreService ds;

    public AppengineTransationDriver(AppengineEnvironmentDriver environment) {
        this.environment = environment;
    }

    private DatastoreService datastore() {
        if (ds == null) {
            ds = DatastoreServiceFactory.getDatastoreService();
        }
        return ds;
    }

    @Override
    public TransactionDriver begin() {
        logger.finer("begin");
        tx = datastore().beginTransaction();
        logger.finer("done");
        return this;
    }

    @Override
    public TransactionDriver beginX() {
        logger.finer("begin X");
        if (!environment.isProduction()) {
            return this;
        }

        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        tx = datastore().beginTransaction(options);
        logger.finer("done");
        return this;
    }

    @Override
    public void rollback() {
        logger.finer("rollback");
        if (tx == null) {
            return;
        }

        if (!tx.isActive()) {
            tx = null;
            return;
        }

        tx.rollback();
        tx = null;
        logger.finer("done");
    }

    @Override
    public void commit() {
        logger.finer("commit");
        if (tx == null) {
            return;
        }

        tx.commit();
        tx = null;
        logger.finer("done");
    }

}
