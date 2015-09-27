package io.yawp.driver.postgresql;

import io.yawp.driver.api.QueryDriver;
import io.yawp.driver.postgresql.connection.ConnectionManager;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;
import io.yawp.driver.postgresql.datastore.PGDatastore;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

public class PGQueryDriver implements QueryDriver {

	private Repository r;

	private PGDatastore datastore;

	public PGQueryDriver(Repository r, ConnectionManager connectionManager) {
		this.r = r;
		this.datastore = PGDatastore.create(connectionManager);
	}

	@Override
	public <T> List<T> objects(QueryBuilder<?> builder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fetch(IdRef<T> id) {
//
//			Key key = IdRefToKey.toKey(r, id);
//			Entity entity = datastore.get(key);
//			return (T) toObject(id.getModel(), entity);
//
//
//		datastore.get(IdRefToKey.toKey(r, id));
		// TODO Auto-generated method stub
		return null;
	}

}
