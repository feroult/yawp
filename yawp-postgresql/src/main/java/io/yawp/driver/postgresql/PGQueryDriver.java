package io.yawp.driver.postgresql;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.driver.api.QueryDriver;
import io.yawp.driver.postgresql.datastore.Datastore;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.EntityNotFoundException;
import io.yawp.driver.postgresql.datastore.FalsePredicateException;
import io.yawp.driver.postgresql.datastore.Key;
import io.yawp.driver.postgresql.datastore.Query;
import io.yawp.driver.postgresql.sql.ConnectionManager;
import io.yawp.repository.FieldModel;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectHolder;
import io.yawp.repository.ObjectModel;
import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PGQueryDriver implements QueryDriver {

	private Repository r;

	private Datastore datastore;

	public PGQueryDriver(Repository r, ConnectionManager connectionManager) {
		this.r = r;
		this.datastore = Datastore.create(connectionManager);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> objects(QueryBuilder<?> builder) {
		try {
			List<Entity> queryResult = generateResults(builder, false);

			List<T> objects = new ArrayList<T>();

			for (Entity entity : queryResult) {
				objects.add((T) toObject(builder.getModel(), entity));
			}

			return objects;
		} catch (FalsePredicateException e) {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) {
		try {
			List<Entity> queryResult = generateResults(builder, false);

			List<IdRef<T>> ids = new ArrayList<IdRef<T>>();

			for (Entity entity : queryResult) {
				ids.add((IdRef<T>) IdRefToKey.toIdRef(r, entity.getKey()));
			}

			return ids;
		} catch (FalsePredicateException e) {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fetch(IdRef<T> id) {
		try {
			Key key = IdRefToKey.toKey(r, id);
			Entity entity = datastore.get(key);
			return (T) toObject(id.getModel(), entity);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	// query

	private List<Entity> generateResults(QueryBuilder<?> builder, boolean keysOnly) throws FalsePredicateException {
		return datastore.query(new Query(builder, keysOnly));
	}

	// to object

	public Object toObject(ObjectModel model, Entity entity) {
		Object object = model.createInstance();

		ObjectHolder objectHolder = new ObjectHolder(object);
		objectHolder.setId(IdRefToKey.toIdRef(r, entity.getKey()));

		List<FieldModel> fieldModels = objectHolder.getModel().getFieldModels();

		for (FieldModel fieldModel : fieldModels) {
			if (fieldModel.isId()) {
				continue;
			}

			safeSetObjectProperty(entity, object, fieldModel);
		}

		return object;
	}

	private <T> void safeSetObjectProperty(Entity entity, T object, FieldModel fieldModel) {
		try {
			setObjectProperty(object, entity, fieldModel, fieldModel.getField());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> void setObjectProperty(T object, Entity entity, FieldModel fieldModel, Field field) throws IllegalAccessException {
		Object value = entity.getProperty(field.getName());

		if (value == null) {
			field.set(object, null);
			return;
		}

		if (fieldModel.isEnum()) {
			setEnumProperty(object, field, value);
			return;
		}

		if (fieldModel.isSaveAsJson()) {
			setJsonProperty(r, object, field, value);
			return;
		}

		if (fieldModel.isInt()) {
			setIntProperty(object, field, value);
			return;
		}

		// overrides
		if (fieldModel.isLong()) {
			setLongProperty(object, field, value);
			return;
		}

		// overrides
		if (fieldModel.isDate()) {
			setDateProperty(object, field, value);
			return;
		}

		if (fieldModel.isIdRef()) {
			setIdRefProperty(r, object, field, value);
			return;
		}

		if (fieldModel.isSaveAsText()) {
			setTextProperty(object, field, value);
			return;
		}

		field.set(object, value);
	}

	private <T> void setIdRefProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, IdRef.parse(r, HttpVerb.GET, (String) value));
	}

	private <T> void setIntProperty(T object, Field field, Object value) throws IllegalAccessException {
		// override
		field.set(object, ((Double) value).intValue());
	}

	// override
	private <T> void setLongProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Double) value).longValue());
	}

	// override
	private <T> void setDateProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, DateUtils.toTimestamp((String) value));
	}

	private <T> void setTextProperty(T object, Field field, Object value) throws IllegalAccessException {
		// override
		field.set(object, ((String) value));
	}

	private <T> void setJsonProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		// override
		String json = (String) value;
		field.set(object, JsonUtils.from(r, json, field.getGenericType()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
	}

}
