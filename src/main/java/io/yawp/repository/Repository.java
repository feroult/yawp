package io.yawp.repository;

import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.actions.RepositoryActions;
import io.yawp.repository.hooks.RepositoryHooks;
import io.yawp.repository.query.DatastoreQuery;
import io.yawp.utils.EntityUtils;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class Repository {

	private RepositoryFeatures repositoryFeatures;

	private Namespace namespace;

	public static Repository r() {
		return new Repository();
	}

	public static Repository r(String ns) {
		return new Repository(ns);
	}

	private Repository() {
		this.namespace = new Namespace();
	}

	private Repository(String ns) {
		this.namespace = new Namespace(ns);
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

	private void saveInternal(Object object) {
		Entity entity = createEntity(object);
		EntityUtils.toEntity(object, entity);
		saveEntity(object, entity);
	}

	public Object action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, Map<String, String> params) {
		namespace.set(clazz);
		try {
			Method actionMethod = repositoryFeatures.get(clazz).getAction(actionKey);
			return RepositoryActions.execute(this, id, actionMethod, params);
		} finally {
			namespace.reset();
		}
	}

	public <T> DatastoreQuery<T> queryWithHooks(Class<T> clazz) {
		DatastoreQuery<T> q = DatastoreQuery.q(clazz, this);
		RepositoryHooks.beforeQuery(this, q, clazz);
		return q;
	}

	public <T> DatastoreQuery<T> query(Class<T> clazz) {
		return DatastoreQuery.q(clazz, this);
	}

	public void destroy(IdRef<?> id) {
		namespace.set(id.getClazz());
		try {
			for (IdRef<?> child : id.children()) {
				destroy(child);
			}
			DatastoreServiceFactory.getDatastoreService().delete(id.asKey());
		} finally {
			namespace.reset();
		}
	}

	private void saveEntity(Object object, Entity entity) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Key key = datastoreService.put(entity);
		EntityUtils.setKey(this, object, key);
	}

	private Entity createEntity(Object object) {
		Key key = EntityUtils.getKey(object);

		if (key == null) {
			return createEntityWithNewKey(object);
		}

		return new Entity(key);
	}

	private Entity createEntityWithNewKey(Object object) {
		Key parentKey = EntityUtils.getParentKey(object);
		if (parentKey == null) {
			return new Entity(EntityUtils.getKindFromClass(object.getClass()));
		}
		return new Entity(EntityUtils.getKindFromClass(object.getClass()), parentKey);
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
}
