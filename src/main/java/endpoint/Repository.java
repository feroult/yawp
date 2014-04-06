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

	public void save(DatastoreObject object) {
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

	public HttpResponse action(Class<? extends DatastoreObject> clazz, String method, String action, long id, Map<String, String> params) {
		namespace.set(clazz);
		try {
			return RepositoryActions.execute(this, clazz, method, action, id, params);
		} finally {
			namespace.reset();
		}
	}

	public <T extends DatastoreObject> List<T> all(Class<T> clazz) {
		namespace.set(clazz);
		try {
			return query(clazz).asList();
		} finally {
			namespace.reset();
		}
	}

	public <T extends DatastoreObject> T findByKey(Key key, Class<T> clazz) {
		namespace.set(clazz);
		try {

			DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

			try {
				Entity entity = datastoreService.get(key);
				T object = EntityUtils.toObject(entity, clazz);
				loadLists(object);
				return object;
			} catch (EntityNotFoundException e) {
				logger.warning("entity not found: " + e.getMessage());
				return null;
			}
		} finally {
			namespace.reset();
		}
	}

	public <T extends DatastoreObject> T findById(long id, Class<T> clazz) {
		namespace.set(clazz);
		try {

			return findByKey(KeyFactory.createKey(EntityUtils.getKind(clazz), id), clazz);
		} finally {
			namespace.reset();
		}
	}

	public <T extends DatastoreObject> DatastoreQuery<T> query(Class<T> clazz) {
		namespace.set(clazz);
		try {

			return new DatastoreQuery<T>(clazz, namespace);
		} finally {
			namespace.reset();
		}
	}

	private void saveEntity(DatastoreObject object, Entity entity, String action) {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Key key = datastoreService.put(entity);
		object.setKey(key);
	}

	@SuppressWarnings("unchecked")
	private void saveLists(DatastoreObject object, Entity entity) {
		Field[] fields = EntityUtils.getFields(object.getClass());
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!EntityUtils.isSaveAsList(field)) {
				continue;
			}

			field.setAccessible(true);

			try {
				saveList(EntityUtils.getListClass(field), (List<DatastoreObject>) field.get(object), object);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void saveList(Class<? extends DatastoreObject> childClazz, List<DatastoreObject> childs, DatastoreObject parentObject) {

		deleteChilds(parentObject.getKey(), childClazz);

		if (childs == null) {
			return;
		}

		for (DatastoreObject child : childs) {
			Entity entity = createEntityForChild(child, parentObject);
			EntityUtils.toEntity(child, entity);
			saveEntity(child, entity, null);
		}
	}

	private void deleteChilds(Key parentKey, Class<? extends DatastoreObject> childClazz) {
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();

		Query query = new Query(EntityUtils.getKind(childClazz));

		query.setAncestor(parentKey);
		query.setKeysOnly();

		Iterable<Entity> childs = service.prepare(query).asIterable();

		for (Entity child : childs) {
			service.delete(child.getKey());
		}

	}

	private Entity createEntity(DatastoreObject object) {
		Entity entity = null;

		if (object.getKey() == null) {
			entity = new Entity(EntityUtils.getKind(object.getClass()));
		} else {
			Key key = KeyFactory.createKey(object.getKey().getKind(), object.getKey().getId());
			entity = new Entity(key);
		}
		return entity;
	}

	private Entity createEntityForChild(DatastoreObject object, DatastoreObject parent) {
		Entity entity = null;

		if (object.getKey() == null) {
			entity = new Entity(EntityUtils.getKind(object.getClass()), parent.getKey());
		} else {
			Key key = KeyFactory.createKey(parent.getKey(), object.getKey().getKind(), object.getKey().getId());
			entity = new Entity(key);
		}
		return entity;
	}

	private void loadLists(DatastoreObject object) {
		Field[] fields = EntityUtils.getFields(object.getClass());
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!EntityUtils.isSaveAsList(field)) {
				continue;
			}

			field.setAccessible(true);

			List<DatastoreObject> list = new ArrayList<DatastoreObject>();
			list.addAll(query(EntityUtils.getListClass(field)).parentKey(object.getKey()).asList());

			try {
				field.set(object, list);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
