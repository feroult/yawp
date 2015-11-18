package io.yawp.driver.api;

public interface TransactionDriver {

    TransactionDriver begin();

    TransactionDriver beginX();

    void rollback();

    void commit();

}
