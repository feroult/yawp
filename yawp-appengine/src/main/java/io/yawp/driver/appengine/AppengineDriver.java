package io.yawp.driver.appengine;

import io.yawp.driver.api.*;
import io.yawp.driver.appengine.pipes.AppenginePipesDriver;
import io.yawp.repository.Repository;

public class AppengineDriver implements Driver {

    private Repository r;

    @Override
    public void init(Repository r) {
        this.r = r;
    }

    @Override
    public PersistenceDriver persistence() {
        return new AppenginePersistenceDriver(r);
    }

    @Override
    public QueryDriver query() {
        return new AppengineQueryDriver(r);
    }

    @Override
    public NamespaceDriver namespace() {
        return new AppengineNamespaceDriver();
    }

    @Override
    public TransactionDriver transaction() {
        return new AppengineTransationDriver(new AppengineEnvironmentDriver());
    }

    @Override
    public EnvironmentDriver environment() {
        return new AppengineEnvironmentDriver();
    }

    @Override
    public HelpersDriver helpers() {
        return new AppengineHelpersDriver();
    }

    @Override
    public PipesDriver pipes() {
        throw new DriverNotImplementedException();
        //return new AppenginePipesDriver(r);
    }
}
