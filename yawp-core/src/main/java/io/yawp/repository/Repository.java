package io.yawp.repository;

import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.RepositoryActions;
import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.repository.pipes.RepositoryPipes;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.Map;

public class Repository implements RepositoryApi {

    private RepositoryFeatures repositoryFeatures;

    private RequestContext requestContext;

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

    @Override
    public Repository namespace(String ns) {
        namespace.setNs(ns);
        return this;
    }

    @Override
    public Namespace namespace() {
        return namespace;
    }

    @Override
    public String currentNamespace() {
        return namespace.getNs();
    }

    @Override
    public Repository setFeatures(RepositoryFeatures repositoryFeatures) {
        this.repositoryFeatures = repositoryFeatures;
        return this;
    }

    @Override
    public Repository setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
        return this;
    }

    @Override
    public Driver driver() {
        if (driver != null) {
            return driver;
        }
        driver = DriverFactory.getDriver(this);
        return driver;
    }

    @Override
    public AsyncRepository async() {
        return new AsyncRepository(this);
    }

    @Override
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

    @Override
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
        boolean newTransaction = beginTransactionForPipes(object);
        driver().persistence().save(object);
        fluxPipes(object);
        if (newTransaction) {
            commit();
        }
    }

    private boolean beginTransactionForPipes(Object object) {
        Class<?> endpointClazz = object.getClass();
        return beginTransactionForPipes(endpointClazz);
    }

    private boolean beginTransactionForPipes(IdRef<?> id) {
        Class<?> endpointClazz = id.getClazz();
        return beginTransactionForPipes(endpointClazz);
    }

    private boolean beginTransactionForPipes(Class<?> endpointClazz) {
        if (!RepositoryPipes.hasPipes(this, endpointClazz)) {
            return false;
        }
        if (isTransationInProgress()) {
            return false;
        }
        begin();
        return true;
    }

    private void fluxPipes(Object object) {
        // TODO: pipes - Deal with transactions. Pipes should be transactional with saving.
        RepositoryPipes.flux(this, object);
    }

    private void refluxPipes(IdRef<?> id, StringBuilder sb) {
        // TODO: pipes - Deal with transactions, load existing object only one time (shield may load it too)
        RepositoryPipes.reflux(this, id, sb);
    }

    private <T> FutureObject<T> saveInternalAsync(T object, boolean enableHooks) {
        FutureObject<T> futureObject = driver().persistence().saveAsync(object);
        futureObject.setEnableHooks(enableHooks);
        return futureObject;
    }

    @Override
    public Object action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, String json, Map<String, String> params) {
        namespace.set(clazz);
        try {
            ActionMethod actionMethod = repositoryFeatures.getByClazz(clazz).getAction(actionKey);
            return RepositoryActions.execute(this, actionMethod, id, json, params);
        } finally {
            namespace.reset();
        }
    }

    @Override
    public <T> QueryBuilder<T> queryWithHooks(Class<T> clazz) {
        QueryBuilder<T> q = QueryBuilder.q(clazz, this);
        RepositoryHooks.beforeQuery(this, q, clazz);
        return q;
    }

    @Override
    public <T> QueryBuilder<T> query(Class<T> clazz) {
        return QueryBuilder.q(clazz, this);
    }

    @Override
    public void destroy(IdRef<?> id) {
        StringBuilder sb = new StringBuilder();
        sb.append("destroy -> " + id + " -- " + java.lang.System.identityHashCode(this) + " -- thread " + Thread.currentThread().getName() + " -- ");

        namespace.set(id.getClazz());
        try {
            RepositoryHooks.beforeDestroy(this, id);

            boolean newTransaction = beginTransactionForPipes(id);
            refluxPipes(id, sb);
            driver().persistence().destroy(id);
            if (newTransaction) {
                sb.append(" -- before commit -- ");
                commit();
                sb.append(" -- commit ok -- ");
            }

            RepositoryHooks.afterDestroy(this, id);
        } catch (Throwable t) {
            sb.append(" -- exception -> " + t.getMessage() + " -- ");
            throw t;
        } finally {
            sb.append(" -- finally -- ");
            System.out.println(sb.toString());
            namespace.reset();
        }
    }

    @Override
    public Class<?> getClazzByKind(String kind) {
        return repositoryFeatures.getClazzByKind(kind);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> EndpointFeatures<T> getEndpointFeatures(Class<T> endpoint) {
        return (EndpointFeatures<T>) repositoryFeatures.getByClazz(endpoint);
    }

    @Override
    public EndpointFeatures<?> getEndpointFeatures(String endpointPath) {
        return repositoryFeatures.getByPath(endpointPath);
    }

    @Override
    public RepositoryFeatures getFeatures() {
        return repositoryFeatures;
    }

    @Override
    public <T> IdRef<T> parseId(Class<T> clazz, String idString) {
        return IdRef.parse(clazz, this, idString);
    }

    @Override
    public <T> List<IdRef<T>> parseIds(Class<T> clazz, List<String> idsString) {
        return IdRef.parse(clazz, this, idsString);
    }

    @Override
    public void begin() {
        tx = driver().transaction().begin();
    }

    @Override
    public void beginX() {
        tx = driver().transaction().beginX();
    }

    @Override
    public void rollback() {
        if (tx == null) {
            throw new RuntimeException("No transaction in progress");
        }
        try {
            tx.rollback();
        } finally {
            tx = null;
        }
    }

    @Override
    public void commit() {
        if (tx == null) {
            throw new RuntimeException("No transaction in progress");
        }
        try {
            tx.commit();
        } finally {
            tx = null;
        }
    }

    @Override
    public boolean isTransationInProgress() {
        return tx != null;
    }

    @Override
    public TransactionDriver currentTransaction() {
        return this.tx;
    }

    @Override
    public RequestContext getRequestContext() {
        return requestContext;
    }
}
