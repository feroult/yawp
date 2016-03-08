package io.yawp.driver.appengine;

import com.google.appengine.api.datastore.*;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.driver.api.PersistenceDriver;
import io.yawp.repository.FutureObject;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.models.FieldModel;
import io.yawp.repository.models.ObjectHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
    public void save(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);
        Entity entity = createEntity(objectHolder);
        toEntity(objectHolder, entity);
        saveEntity(objectHolder, entity);
    }

    @Override
    public <T> FutureObject<T> saveAsync(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);
        Entity entity = createEntity(objectHolder);
        toEntity(objectHolder, entity);
        return saveEntityAsync(objectHolder, entity);
    }

    @Override
    public void destroy(IdRef<?> id) {
        datastore().delete(IdRefToKey.toKey(r, id));
    }

    private Entity createEntity(ObjectHolder objectHolder) {
        IdRef<?> id = objectHolder.getId();

        if (id == null) {
            return createEntityWithNewKey(objectHolder);
        }

        return new Entity(IdRefToKey.toKey(r, id));
    }

    private Entity createEntityWithNewKey(ObjectHolder objectHolder) {
        IdRef<?> parentId = objectHolder.getParentId();

        if (parentId == null) {
            return new Entity(objectHolder.getModel().getKind());
        }
        return new Entity(objectHolder.getModel().getKind(), IdRefToKey.toKey(r, parentId));
    }

    private void saveEntity(ObjectHolder objectHolder, Entity entity) {
        Key key = datastore().put(entity);
        objectHolder.setId(IdRefToKey.toIdRef(r, key, objectHolder.getModel()));
    }

    @SuppressWarnings("unchecked")
    private <T> FutureObject<T> saveEntityAsync(ObjectHolder objectHolder, Entity entity) {
        Future<Key> futureKey = asyncDatastore().put(entity);
        return new FutureObject<T>(r, new FutureIdRef(r, futureKey, objectHolder.getModel()), (T) objectHolder.getObject());
    }

    public void toEntity(ObjectHolder objectHolder, Entity entity) {
        List<FieldModel> fieldModels = objectHolder.getModel().getFieldModels();

        for (FieldModel fieldModel : fieldModels) {
            if (fieldModel.isId()) {
                continue;
            }

            if (fieldModel.isTransient()) {
                continue;
            }

            setEntityProperty(objectHolder, entity, fieldModel);
        }
    }

    private void setEntityProperty(ObjectHolder objectHolder, Entity entity, FieldModel fieldModel) {
        Object value = getFieldValue(fieldModel, objectHolder);

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

    private Object getFieldValue(FieldModel fieldModel, ObjectHolder objectHolder) {
        Object value = fieldModel.getValue(objectHolder.getObject());

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

        if (fieldModel.isListOfIds()) {
            return convertToListOfUris((List<IdRef<?>>) value);
        }

        return value;
    }

    private List<String> convertToListOfUris(List<IdRef<?>> ids) {
        List<String> uris = new ArrayList<>(ids.size());
        for (IdRef<?> id : ids) {
            uris.add(id.getUri());
        }
        return uris;
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
