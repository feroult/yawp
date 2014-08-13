package endpoint;

import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import endpoint.actions.RepositoryActions;
import endpoint.hooks.RepositoryHooks;
import endpoint.query.DatastoreQuery;
import endpoint.response.HttpResponse;
import endpoint.utils.EntityUtils;

public class Repository {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Repository.class.getSimpleName());

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

	public void save(Object object) {
		namespace.set(object.getClass());
		try {
			RepositoryHooks.beforeSave(this, object);

			Entity entity = createEntity(object);
			EntityUtils.toEntity(object, entity);
			saveEntity(object, entity, null);

			RepositoryHooks.afterSave(this, object);
		} finally {
			namespace.reset();
		}
	}

	public HttpResponse action(Class<?> clazz, String method, String action, Long id, Map<String, String> params) {
		namespace.set(clazz);
		try {
			return RepositoryActions.execute(this, clazz, method, action, id, params);
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
		Entity entity = null;

		Key currentKey = EntityUtils.getKey(object);

		if (currentKey == null) {
			entity = new Entity(EntityUtils.getKind(object.getClass()));
		} else {
			Key key = KeyFactory.createKey(currentKey.getKind(), currentKey.getId());
			entity = new Entity(key);
		}
		return entity;
	}
}
