package io.yawp.repository;

import io.yawp.commons.config.Config;
import io.yawp.commons.config.FeaturesConfig;
import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.tools.scanner.RepositoryScanner;

import java.util.List;
import java.util.Map;

public class Yawp extends ThreadLocal<Repository> implements RepositoryApi {

    public static Yawp yawp = new Yawp();

    private static RepositoryFeatures features;

    public static <T> QueryBuilder<T> yawp(Class<T> clazz) {
        yawp.init();
        return yawp.get().query(clazz);
    }

    private void init() {
        if (yawp.get() != null) {
            return;
        }
        safeLoadFeatures();
        yawp.set(Repository.r().setFeatures(features));
    }

    private void safeLoadFeatures() {
        if (features != null) {
            return;
        }

        synchronized (yawp) {
            Config config = Config.load();
            FeaturesConfig featuresConfig = config.getDefaultFeatures();
            RepositoryScanner scanner = new RepositoryScanner(featuresConfig.getPackagePrefix());
            scanner.enableHooks(featuresConfig.isEnableHooks());
            features = scanner.scan();
        }
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
    public <T> T saveWithHooks(T object) {
        init();
        return get().saveWithHooks(object);
    }

    @Override
    public <T> T save(T object) {
        init();
        return get().save(object);
    }

    @Override
    public Object action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, String json, Map<String, String> params) {
        init();
        return get().action(id, clazz, actionKey, json, params);
    }

    @Override
    public <T> QueryBuilder<T> queryWithHooks(Class<T> clazz) {
        init();
        return get().queryWithHooks(clazz);
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
    public boolean isTransationInProgress() {
        init();
        return get().isTransationInProgress();
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
