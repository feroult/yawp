package endpoint.repository;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import endpoint.repository.actions.ActionKey;
import endpoint.repository.actions.RepositoryActions;
import endpoint.repository.hooks.RepositoryHooks;
import endpoint.repository.query.DatastoreQuery;
import endpoint.repository.response.HttpResponse;
import endpoint.utils.EntityUtils;

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

	public void saveWithHooks(Object object) {
		namespace.set(object.getClass());
		try {
			RepositoryHooks.beforeSave(this, object);
			saveInternal(object);
			RepositoryHooks.afterSave(this, object);
		} finally {
			namespace.reset();
		}
	}

	public void save(Object object) {
		namespace.set(object.getClass());
		try {
			saveInternal(object);
		} finally {
			namespace.reset();
		}
	}

	private void saveInternal(Object object) {
		Entity entity = createEntity(object);
		EntityUtils.toEntity(object, entity);
		saveEntity(object, entity, null);
	}

	public HttpResponse action(IdRef<?> id, Class<?> clazz, ActionKey actionKey, Map<String, String> params) {
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

	public void delete(Object object) {
		delete(EntityUtils.getId(object), object.getClass());
	}

	public void delete(Long id, Class<?> clazz) {
		namespace.set(clazz);
		try {
			DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

			Key key = EntityUtils.createKey(id, clazz);
			datastoreService.delete(key);

		} finally {
			namespace.reset();
		}

	}

	private void saveEntity(Object object, Entity entity, String action) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Key key = datastoreService.put(entity);
		EntityUtils.setKey(this, object, key);
	}

	private Entity createEntity(Object object) {
		Key currentKey = EntityUtils.getKey(object);
		Key parentKey = EntityUtils.getParentKey(object);
		if (parentKey == null) {
			IdRef<?> id = EntityUtils.getIdRef(object);
			if (id != null) {
				parentKey = EntityUtils.createKey(id.getParentId());
			}
		}

		if (currentKey == null) {
			return new Entity(EntityUtils.getKindFromClass(object.getClass()), parentKey);
		}

		Key key = KeyFactory.createKey(parentKey, currentKey.getKind(), currentKey.getId());
		return new Entity(key);
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
