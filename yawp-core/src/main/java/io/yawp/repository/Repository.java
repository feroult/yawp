package io.yawp.repository;

import io.yawp.driver.api.Driver;
import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.RepositoryActions;
import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.Map;

public class Repository {

    private RepositoryFeatures repositoryFeatures;

    private Namespace namespace;

    private Driver driver;

    private TransactionDriver tx;

    public static Repository r() {
        return new Repository();
    }

    public static Repository r(String ns) {
        return new Repository(ns);
    }

    private Repository() {
        this.namespace = new Namespace(driver().namespace());
    }

    private Repository(String ns) {
        this.namespace = new Namespace(ns, driver().namespace());
    }

    public Repository namespace(String ns) {
        namespace.setNs(ns);
        return this;
    }

    public Namespace namespace() {
        return namespace;
    }

    public String currentNamespace() {
        return namespace.getNs();
    }

    public Repository setFeatures(RepositoryFeatures repositoryFeatures) {
        this.repositoryFeatures = repositoryFeatures;
        return this;
    }

    public Driver driver() {
        if (driver != null) {
            return driver;
        }
        driver = DriverFactory.getDriver(this);
        return driver;
    }

    public AsyncRepository async() {
        return new AsyncRepository(this);
    }

    public <T> T saveWithHooks(T object) {
        namespace.set(object.getClass());
        try {
            RepositoryHooks.beforeSave(this, object);
            saveInternal(object);
            RepositoryHooks.afterSave(this, object);
        } finally {
            namespace.reset();
        }
        return object;
    }

    public <T> T save(T object) {
        namespace.set(object.getClass());
        try {
            saveInternal(object);
        } finally {
            namespace.reset();
        }
        return object;
    }

    protected <T> FutureObject<T> saveAsyncWithHooks(T object) {
        namespace.set(object.getClass());
        try {
            RepositoryHooks.beforeSave(this, object);
            FutureObject<T> future = saveInternalAsync(object, true);
            return future;
        } finally {
            namespace.reset();
        }
    }

    protected <T> FutureObject<T> saveAsync(T object) {
        namespace.set(object.getClass());
        try {
            FutureObject<T> future = saveInternalAsync(object, false);
            return future;
        } finally {
            namespace.reset();
        }
    }

    private void saveInternal(Object object) {
        driver().persistence().save(object);
    }

    private <T> FutureObject<T> saveInternalAsync(T object, boolean enableHooks) {
        FutureObject<T> futureObject = driver().persistence().saveAsync(object);
        futureObject.setEnableHooks(enableHooks);
        return futureObject;
    }

    public Object action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, String json, Map<String, String> params) {
        namespace.set(clazz);
        try {
            ActionMethod actionMethod = repositoryFeatures.get(clazz).getAction(actionKey);
            return RepositoryActions.execute(this, actionMethod, id, json, params);
        } finally {
            namespace.reset();
        }
    }

    public <T> QueryBuilder<T> queryWithHooks(Class<T> clazz) {
        QueryBuilder<T> q = QueryBuilder.q(clazz, this);
        RepositoryHooks.beforeQuery(this, q, clazz);
        return q;
    }

    public <T> QueryBuilder<T> query(Class<T> clazz) {
        return QueryBuilder.q(clazz, this);
    }

    public void destroy(IdRef<?> id) {
        namespace.set(id.getClazz());
        try {
            RepositoryHooks.beforeDestroy(this, id);
            driver().persistence().destroy(id);
            RepositoryHooks.afterDestroy(this, id);
        } finally {
            namespace.reset();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> EndpointFeatures<T> getEndpointFeatures(Class<T> endpoint) {
        return (EndpointFeatures<T>) repositoryFeatures.get(endpoint);
    }

    public EndpointFeatures<?> getEndpointFeatures(String endpointName) {
        return repositoryFeatures.get(endpointName);
    }

    public RepositoryFeatures getFeatures() {
        return repositoryFeatures;
    }

    public Driver getDriver() {
        return driver();
    }

    public <T> IdRef<T> parseId(Class<T> clazz, String idString) {
        return IdRef.parse(clazz, this, idString);
    }

    public <T> List<IdRef<T>> parseIds(Class<T> clazz, List<String> idsString) {
        return IdRef.parse(clazz, this, idsString);
    }

    public void begin() {
        tx = driver().transaction().begin();
    }

    public void beginX() {
        tx = driver().transaction().beginX();
    }

    public void rollback() {
        if (tx == null) {
            throw new RuntimeException("No transaction in progress");
        }

        tx.rollback();
        tx = null;
    }

    public void commit() {
        if (tx == null) {
            throw new RuntimeException("No transaction in progress");
        }
        tx.commit();
        tx = null;
    }

    public boolean isTransationInProgress() {
        return tx != null;
    }

    public TransactionDriver currentTransaction() {
        return this.tx;
    }

}
