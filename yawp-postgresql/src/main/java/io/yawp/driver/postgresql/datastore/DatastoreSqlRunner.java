package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.sql.PlaceHolder;
import io.yawp.driver.postgresql.sql.SqlRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.util.PGobject;

public class DatastoreSqlRunner extends SqlRunner {

    public DatastoreSqlRunner(String kind, String sql) {
        super(sql.replaceAll(":kind", kind));
    }

    protected final void bind(String placeHolderKey, Object value) {
        if (value instanceof Key) {
            bind(placeHolderKey, (Key) value);
            return;
        }
        super.bind(placeHolderKey, value);
    }

    protected final void bind(String placeHolderKey, Key key) {
        PlaceHolder placeHolderObject = new PlaceHolder(createJsonObject(key.serialize()));
        bind(placeHolderKey, placeHolderObject);
    }

    protected final void bind(String placeHolderKey, Entity entity) {
        PlaceHolder placeHolderObject = new PlaceHolder(createJsonObject(entity.serializeProperties()));
        bind(placeHolderKey, placeHolderObject);
    }

    protected final Entity getEntity(ResultSet rs) throws SQLException {
        PGobject keyObject = (PGobject) rs.getObject("key");
        PGobject propertiesObject = (PGobject) rs.getObject("properties");

        Entity entity = new Entity(Key.deserialize(keyObject.getValue()));
        entity.deserializeProperties(propertiesObject.getValue());

        return entity;
    }

    protected final Object getEntities(ResultSet rs) throws SQLException {
        List<Entity> entities = new ArrayList<Entity>();

        while (rs.next()) {
            entities.add(getEntity(rs));
        }

        return entities;
    }

}
