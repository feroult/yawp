package io.yawp.repository.driver.appengine;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.FieldModel;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ObjectHolder;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.driver.api.RepositoryDriver;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

public class AppengineRepositoryDriver implements RepositoryDriver {

	private static final String NORMALIZED_FIELD_PREFIX = "__";

	private DatastoreService datastore;

	private Repository r;

	@Override
	public void init(Repository r) {
		this.r = r;
	}

	public DatastoreService datastore() {
		if (datastore != null) {
			return datastore;
		}
		datastore = DatastoreServiceFactory.getDatastoreService();
		return datastore;
	}

	@Override
	public void save(ObjectHolder objectH) {
		Entity entity = createEntity(objectH);
		toEntity(objectH, entity);
		saveEntity(objectH, entity);
	}

	private Entity createEntity(ObjectHolder objectH) {
		IdRef<?> id = objectH.getId();

		if (id == null) {
			return createEntityWithNewKey(objectH);
		}

		return new Entity(id.asKey());
	}

	private Entity createEntityWithNewKey(ObjectHolder objectH) {
		IdRef<?> parentId = objectH.getParentId();

		if (parentId == null) {
			return new Entity(objectH.getModel().getKind());
		}
		return new Entity(objectH.getModel().getKind(), parentId.asKey());
	}

	private void saveEntity(ObjectHolder objectH, Entity entity) {
		Key key = datastore().put(entity);
		objectH.setId(IdRef.fromKey(r, key));
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
