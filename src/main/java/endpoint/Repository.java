package endpoint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import endpoint.actions.RepositoryActions;
import endpoint.hooks.RepositoryHooks;
import endpoint.response.HttpResponse;
import endpoint.utils.EntityUtils;

public class Repository {

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
			saveLists(object, entity);

			RepositoryHooks.afterSave(this, object);
		} finally {
			namespace.reset();
		}
	}

	public HttpResponse action(Class<?> clazz, String method, String action, long id, Map<String, String> params) {
		namespace.set(clazz);
		try {
			return RepositoryActions.execute(this, clazz, method, action, id, params);
		} finally {
			namespace.reset();
		}
	}

	public <T> List<T> all(Class<T> clazz) {
		namespace.set(clazz);
		try {
			return query(clazz).list().now();
		} finally {
			namespace.reset();
		}
	}

	public <T> DatastoreResult<T> find(Class<T> clazz, Key key) {
		namespace.set(clazz);
		try {

			DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

			try {
				Entity entity = datastoreService.get(key);
				T object = EntityUtils.toObject(entity, clazz);
				loadLists(object);
				return new DatastoreResult<T>(object);
			} catch (EntityNotFoundException e) {
				logger.warning("entity not found: " + e.getMessage());
				return new DatastoreResult<T>();
			}
		} finally {
			namespace.reset();
		}
	}

	public <T> DatastoreResult<T> find(Class<T> clazz, long id) {
		namespace.set(clazz);
		try {

			return find(clazz, KeyFactory.createKey(EntityUtils.getKind(clazz), id));
		} finally {
			namespace.reset();
		}
	}

	public <T> DatastoreQuery<T> query(Class<T> clazz) {
		namespace.set(clazz);
		try {

			return new DatastoreQuery<T>(clazz, namespace);
		} finally {
			namespace.reset();
		}
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
			deleteLists(key, clazz);

		} finally {
			namespace.reset();
		}

	}

	private void deleteLists(Key key, Class<?> clazz) {
		Field[] fields = EntityUtils.getFields(clazz);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!EntityUtils.isSaveAsList(field)) {
				continue;
			}

			field.setAccessible(true);
			deleteChilds(key, EntityUtils.getListClass(field));
		}
	}

	private void saveEntity(Object object, Entity entity, String action) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Key key = datastoreService.put(entity);
		EntityUtils.setKey(object, key);
	}

	@SuppressWarnings("unchecked")
	private void saveLists(Object object, Entity entity) {
		Field[] fields = EntityUtils.getFields(object.getClass());
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!EntityUtils.isSaveAsList(field)) {
				continue;
			}

			field.setAccessible(true);

			try {
				saveList(EntityUtils.getListClass(field), (List<Object>) field.get(object), object);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void saveList(Class<?> childClazz, List<Object> childs, Object parentObject) {

		deleteChilds(EntityUtils.getKey(parentObject), childClazz);

		if (childs == null) {
			return;
		}

		for (Object child : childs) {
			Entity entity = createEntityForChild(child, parentObject);
			EntityUtils.toEntity(child, entity);
			saveEntity(child, entity, null);
		}
	}

	private void deleteChilds(Key parentKey, Class<?> childClazz) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();

		Query query = new Query(EntityUtils.getKind(childClazz));

		query.setAncestor(parentKey);
		query.setKeysOnly();

		Iterable<Entity> childs = service.prepare(query).asIterable();

		for (Entity child : childs) {
			service.delete(child.getKey());
		}

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

	private Entity createEntityForChild(Object object, Object parent) {
		Entity entity = null;

		Key currentKey = EntityUtils.getKey(object);
		if (currentKey == null) {
			entity = new Entity(EntityUtils.getKind(object.getClass()), EntityUtils.getKey(parent));
		} else {
			Key key = KeyFactory.createKey(EntityUtils.getKey(parent), currentKey.getKind(), currentKey.getId());
			entity = new Entity(key);
		}
		return entity;
	}

	private void loadLists(Object object) {
		Field[] fields = EntityUtils.getFields(object.getClass());
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!EntityUtils.isSaveAsList(field)) {
				continue;
			}

			field.setAccessible(true);

			List<Object> list = new ArrayList<Object>();
			list.addAll(query(EntityUtils.getListClass(field)).parent(EntityUtils.getKey(object)).list().now());

			try {
				field.set(object, list);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
