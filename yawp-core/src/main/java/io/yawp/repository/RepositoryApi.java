package io.yawp.repository;

import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.TransactionDriver;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;
import java.util.Map;

public interface RepositoryApi {
    Repository namespace(String ns);

    Namespace namespace();

    String currentNamespace();

    Repository setFeatures(RepositoryFeatures repositoryFeatures);

    Repository setRequestContext(RequestContext requestContext);

    Driver driver();

    AsyncRepository async();

    <T> T saveWithHooks(T object);

    <T> T save(T object);

    <T> T fetch(IdRef<T> id);

    <T> FutureObject<T> fetchAsync(IdRef<T> id);

    Object action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, String json, Map<String, String> params);

    <T> QueryBuilder<T> queryWithHooks(Class<T> clazz);

    <T> QueryBuilder<T> query(Class<T> clazz);

    void destroy(IdRef<?> id);

    Class<?> getClazzByKind(String kind);

    @SuppressWarnings("unchecked")
    <T> EndpointFeatures<T> getEndpointFeatures(Class<T> endpoint);

    EndpointFeatures<?> getEndpointFeatures(String endpointPath);

    RepositoryFeatures getFeatures();

    <T> IdRef<T> parseId(Class<T> clazz, String idString);

    <T> List<IdRef<T>> parseIds(Class<T> clazz, List<String> idsString);

    void begin();

    void beginX();

    void rollback();

    void commit();

    boolean isTransationInProgress();

    TransactionDriver currentTransaction();

    RequestContext getRequestContext();
}
