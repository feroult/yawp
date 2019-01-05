package io.yawp.repository;

import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.ActionMethod;
import io.yawp.repository.actions.RepositoryActions;
import io.yawp.repository.features.EndpointFeatures;
import io.yawp.repository.features.RepositoryFeatures;
import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.pipes.RepositoryPipes;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.servlet.cache.Cache;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Repository implements RepositoryApi {

    private final static Logger logger = Logger.getLogger(Repository.class.getName());

    private RepositoryFeatures repositoryFeatures;

    private RequestContext requestContext;

    private Namespace namespace;

    private Driver driver;

    private TransactionDriver tx;

    private Repository() {
        this.namespace = new Namespace(driver().namespace());
    }

    private Repository(String ns) {
        this.namespace = new Namespace(ns, driver().namespace());
    }

    public static Repository r() {
        return new Repository();
    }

    public static Repository r(String ns) {
        return new Repository(ns);
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
    public <T> T save(T object) {
        logger.finer("saving object");

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
    public <T> T fetch(IdRef<T> id) {
        namespace.set(id.getClazz());
        try {
            return driver().query().fetch(id);
        } finally {
            namespace.reset();
        }
    }

    @Override
    public <T> FutureObject<T> fetchAsync(IdRef<T> id) {
        namespace.set(id.getClazz());
        try {
            return driver().query().fetchAsync(id);
        } finally {
            namespace.reset();
        }
    }

    protected <T> FutureObject<T> saveAsync(T object) {
        namespace.set(object.getClass());
        try {
            RepositoryHooks.beforeSave(this, object);
            return saveInternalAsync(object);
        } finally {
            namespace.reset();
        }
    }

    private void saveInternal(Object object) {
        boolean newTransaction = beginTransactionForPipesOnSave(object);
        try {
            updateExistingPipes(object);
            rawSave(object);
            invalidateCache(object);
            fluxPipes(object);
            if (newTransaction) {
                commit();
            }
        } finally {
            if (newTransaction && isTransactionInProgress()) {
                rollback();
            }
        }

        logger.finer("saved");
    }

    private void rawSave(Object object) {
        driver().persistence().save(object);
    }

    private boolean beginTransactionForPipesOnSave(Object object) {
        Class<?> endpointClazz = object.getClass();

        if (!RepositoryPipes.isPipeSourceOrSink(this, endpointClazz)) {
            return false;
        }

        return beginTransactionForPipes();
    }

    private boolean beginTransactionForPipesOnDestroy(IdRef<?> id) {
        Class<?> endpointClazz = id.getClazz();

        if (!RepositoryPipes.isPipeSource(this, endpointClazz)) {
            return false;
        }

        return beginTransactionForPipes();
    }

    private boolean beginTransactionForPipes() {
        if (isTransactionInProgress()) {
            return false;
        }
        begin();
        return true;
    }

    private void fluxPipes(Object object) {
        RepositoryPipes.flux(this, object);
    }

    private void refluxPipes(IdRef<?> id) {
        RepositoryPipes.reflux(this, id);
    }

    private void updateExistingPipes(Object object) {
        RepositoryPipes.updateExisting(this, object);
    }

    private <T> FutureObject<T> saveInternalAsync(T object) {
        FutureObject<T> futureObject = driver().persistence().saveAsync(object);
        futureObject.setHook(new FutureObjectHook<T>() {
            @Override
            public void apply(Repository r, T object) {
                invalidateCache(object);
                RepositoryHooks.afterSave(r, object);
            }
        });
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
    public <T> QueryBuilder<T> query(Class<T> clazz) {
        return QueryBuilder.q(clazz, this);
    }

    @Override
    public void destroy(IdRef<?> id) {
        namespace.set(id.getClazz());
        try {
            RepositoryHooks.beforeDestroy(this, id);
            destroyInternal(id);
            RepositoryHooks.afterDestroy(this, id);
        } finally {
            namespace.reset();
        }
    }

    protected FutureObject<Void> destroyAsync(final IdRef<?> id) {
        namespace.set(id.getClazz());
        try {
            RepositoryHooks.beforeDestroy(this, id);
            return destroyInternalAsync(id);
        } finally {
            namespace.reset();
        }
    }

    private void destroyInternal(IdRef<?> id) {
        boolean newTransaction = beginTransactionForPipesOnDestroy(id);
        try {
            refluxPipes(id);
            driver().persistence().destroy(id);
            invalidateCache(id);
            // TODO: Pipes - cleanup sinks
            if (newTransaction) {
                commit();
            }
        } finally {
            if (newTransaction && isTransactionInProgress()) {
                rollback();
            }
        }
    }

    private FutureObject<Void> destroyInternalAsync(IdRef<?> id) {
        FutureObject<Void> futureObject = driver().persistence().destroyAsync(id);
        futureObject.setHook(new FutureObjectHook<Void>() {
            @Override
            public void apply(Repository r, Void object) {
                invalidateCache(id);
                RepositoryHooks.afterDestroy(r, id);
            }
        });
        return futureObject;
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

    private <T> void invalidateCache(IdRef<T> id) {
        Cache.clear(id);
    }

    private void invalidateCache(Object object) {
        Cache.clear(new ObjectHolder(object).getId());
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
    public boolean isTransactionInProgress() {
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
