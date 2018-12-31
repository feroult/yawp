package io.yawp.repository;

import io.yawp.commons.config.ConfigFile;
import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.features.EndpointFeatures;
import io.yawp.repository.features.Feature;
import io.yawp.repository.features.RepositoryFeatures;
import io.yawp.repository.features.scanner.ClassloaderScanner;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.Map;

public class Yawp extends ThreadLocal<Repository> implements RepositoryApi {

    public static Yawp yawp = new Yawp();

    private static RepositoryFeatures features;

    public static <T> QueryBuilder<T> yawp(Class<T> clazz) {
        init();
        return yawp.get().query(clazz);
    }

    public static Repository yawp() {
        init();
        return yawp.get();
    }

    public static void destroyFeatures() {
        features = null;
    }

    public static void dispose() {
        yawp.set(null);
    }

    public static <T extends Feature> T feature(Class<T> clazz) {
        try {
            T feature = clazz.newInstance();
            feature.setRepository(yawp());
            return feature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void init() {
        if (yawp.get() != null) {
            return;
        }
        if (features == null) {
            safeLoadFeaturesFromConfig();
        }
        yawp.set(Repository.r().setFeatures(features));
    }

    public static void init(String packagePrefix) {
        if (yawp.get() != null) {
            return;
        }
        if (features == null) {
            safeLoadFeatures(packagePrefix);
        }
        yawp.set(Repository.r().setFeatures(features));
    }

    private static synchronized void safeLoadFeaturesFromConfig() {
        if (features != null) {
            return;
        }

        ConfigFile configFile = ConfigFile.load();
        safeLoadFeatures(configFile.getConfig().getPackages());
    }

    private static synchronized void safeLoadFeatures(String packagePrefix) {
        if (features != null) {
            return;
        }

        ClassloaderScanner scanner = new ClassloaderScanner(packagePrefix);
        features = scanner.scan();
    }

    @Override
    public Repository namespace(String ns) {
        init();
        return get().namespace(ns);
    }

    @Override
    public Namespace namespace() {
        init();
        return get().namespace();
    }

    @Override
    public String currentNamespace() {
        init();
        return get().currentNamespace();
    }

    @Override
    public Repository setFeatures(RepositoryFeatures repositoryFeatures) {
        init();
        return get().setFeatures(repositoryFeatures);
    }

    @Override
    public Repository setRequestContext(RequestContext requestContext) {
        init();
        return get().setRequestContext(requestContext);
    }

    @Override
    public Driver driver() {
        init();
        return get().driver();
    }

    @Override
    public AsyncRepository async() {
        init();
        return get().async();
    }

    @Override
    public <T> T save(T object) {
        init();
        return get().save(object);
    }

    @Override
    public <T> T fetch(IdRef<T> id) {
        init();
        return get().fetch(id);

    }

    @Override
    public <T> FutureObject<T> fetchAsync(IdRef<T> id) {
        init();
        return get().fetchAsync(id);
    }

    @Override
    public Object action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, String json, Map<String, String> params) {
        init();
        return get().action(id, clazz, actionKey, json, params);
    }

    @Override
    public <T> QueryBuilder<T> query(Class<T> clazz) {
        init();
        return get().query(clazz);
    }

    @Override
    public void destroy(IdRef<?> id) {
        init();
        get().destroy(id);
    }

    @Override
    public Class<?> getClazzByKind(String kind) {
        init();
        return get().getClazzByKind(kind);
    }

    @Override
    public <T> EndpointFeatures<T> getEndpointFeatures(Class<T> endpoint) {
        init();
        return get().getEndpointFeatures(endpoint);
    }

    @Override
    public EndpointFeatures<?> getEndpointFeatures(String endpointPath) {
        init();
        return get().getEndpointFeatures(endpointPath);
    }

    @Override
    public RepositoryFeatures getFeatures() {
        init();
        return get().getFeatures();
    }

    @Override
    public <T> IdRef<T> parseId(Class<T> clazz, String idString) {
        init();
        return get().parseId(clazz, idString);
    }

    @Override
    public <T> List<IdRef<T>> parseIds(Class<T> clazz, List<String> idsString) {
        init();
        return get().parseIds(clazz, idsString);
    }

    @Override
    public void begin() {
        init();
        get().begin();
    }

    @Override
    public void beginX() {
        init();
        get().beginX();
    }

    @Override
    public void rollback() {
        init();
        get().rollback();
    }

    @Override
    public void commit() {
        init();
        get().commit();
    }

    @Override
    public boolean isTransactionInProgress() {
        init();
        return get().isTransactionInProgress();
    }

    @Override
    public TransactionDriver currentTransaction() {
        init();
        return get().currentTransaction();
    }

    @Override
    public RequestContext getRequestContext() {
        init();
        return get().getRequestContext();
    }
}
