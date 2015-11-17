package io.yawp.driver.api;

public interface TransactionDriver {

    public TransactionDriver begin();

    public TransactionDriver beginX();

    public void rollback();

    public void commit();

}
