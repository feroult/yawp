package io.yawp.repository.driver.appengine;

import io.yawp.commons.utils.FieldModel;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.driver.api.PersistenceDriver;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public class AppenginePersistenceDriver implements PersistenceDriver {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	private Repository r;

	public AppenginePersistenceDriver(Repository r) {
		this.r = r;

	}

	private DatastoreService datastore() {
		return DatastoreServiceFactory.getDatastoreService();
	}

	private AsyncDatastoreService asyncDatastore() {
		return DatastoreServiceFactory.getAsyncDatastoreService();
	}

	@Override
	public void save(ObjectHolder objectH) {
		Entity entity = createEntity(objectH);
		toEntity(objectH, entity);
		saveEntity(objectH, entity);
	}

	@Override
	public <T> FutureObject<T> saveAsync(ObjectHolder objectH, boolean enableHooks) {
		Entity entity = createEntity(objectH);
		toEntity(objectH, entity);
		return saveEntityAsync(objectH, entity, enableHooks);
	}

	@Override
	public void destroy(IdRef<?> id) {
		datastore().delete(IdRefToKey.toKey(r, id));
	}

	private Entity createEntity(ObjectHolder objectH) {
		IdRef<?> id = objectH.getId();

		if (id == null) {
			return createEntityWithNewKey(objectH);
		}

		return new Entity(IdRefToKey.toKey(r, id));
	}

	private Entity createEntityWithNewKey(ObjectHolder objectH) {
		IdRef<?> parentId = objectH.getParentId();

		if (parentId == null) {
			return new Entity(objectH.getModel().getKind());
		}
		return new Entity(objectH.getModel().getKind(), IdRefToKey.toKey(r, parentId));
	}

	private void saveEntity(ObjectHolder objectH, Entity entity) {
		Key key = datastore().put(entity);
		objectH.setId(IdRefToKey.toIdRef(r, key));
	}

	private <T> FutureObject<T> saveEntityAsync(ObjectHolder objectH, Entity entity, boolean enableHooks) {
		Future<Key> futureKey = asyncDatastore().put(entity);
		// TODO: driver - remove enableHooks from here? return only
		// Future<IdRef<?>> ?
		return new FutureObject<T>(r, new FutureIdRef(r, futureKey), objectH, enableHooks);
	}

	public void toEntity(ObjectHolder objectH, Entity entity) {
		List<FieldModel> fieldModels = objectH.getModel().getFieldModels();

		for (FieldModel fieldModel : fieldModels) {
			if (fieldModel.isId()) {
				continue;
			}

			setEntityProperty(objectH, entity, fieldModel);
		}
	}

	private void setEntityProperty(ObjectHolder objectH, Entity entity, FieldModel fieldModel) {
		Object value = getFieldValue(fieldModel, objectH);

		if (!fieldModel.hasIndex()) {
			entity.setUnindexedProperty(fieldModel.getName(), value);
			return;
		}

		if (fieldModel.isIndexNormalizable()) {
			entity.setProperty(NORMALIZED_FIELD_PREFIX + fieldModel.getName(), normalizeValue(value));
			entity.setUnindexedProperty(fieldModel.getName(), value);
			return;
		}

		entity.setProperty(fieldModel.getName(), value);
	}

	private Object getFieldValue(FieldModel fieldModel, ObjectHolder objectH) {
		Object value = fieldModel.getValue(objectH.getObject());

		if (value == null) {
			return null;
		}

		if (fieldModel.isEnum(value)) {
			return value.toString();
		}

		if (fieldModel.isSaveAsJson()) {
			return new Text(JsonUtils.to(value));
		}

		if (fieldModel.isIdRef()) {
			IdRef<?> idRef = (IdRef<?>) value;
			return idRef.getUri();
		}

		if (fieldModel.isSaveAsText()) {
			return new Text(value.toString());
		}

		return value;
	}

	private Object normalizeValue(Object o) {
		if (o == null) {
			return null;
		}

		if (!o.getClass().equals(String.class)) {
			return o;
		}

		return StringUtils.stripAccents((String) o).toLowerCase();
	}
}
