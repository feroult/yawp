package endpoint.repository;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

import endpoint.repository.query.DatastoreQuery;
import endpoint.utils.EntityUtils;

public class IdRef<T> implements Comparable<IdRef<T>> {

	private Class<T> clazz;

	private Long id;

	private Repository r;

	private IdRef<?> parentId;

	protected IdRef(Repository r, Class<T> clazz, Long id) {
		this.clazz = clazz;
		this.id = id;
		this.r = r;
	}

	public void setParentId(IdRef<?> parentId) {
		this.parentId = parentId;
	}

	public T fetch() {
		return fetch(clazz);
	}

	public <TT> TT fetch(Class<TT> childClazz) {
		return r.query(childClazz).id(this);
	}

	public <TT> TT child(Class<TT> childClazz) {
		DatastoreQuery<TT> q = r.query(childClazz).from(this);
		return q.only();
	}

	public Long asLong() {
		return id;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	@SuppressWarnings("unchecked")
	public <TT> IdRef<TT> getParentId() {
		return (IdRef<TT>) parentId;
	}

	public static IdRef<?> fromKey(Repository r, Key key) {
		if (key == null) {
			return null;
		}
		Class<?> objectClass = EntityUtils.getClassFromKind(key.getKind());
		IdRef<?> ref = IdRef.create(r, EntityUtils.getIdType(objectClass), key.getId());
		ref.parentId = fromKey(r, key.getParent());
		return ref;
	}

	public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, Long id) {
		return new IdRef<TT>(r, clazz, id);
	}

	public static <TT> List<IdRef<TT>> create(Repository r, Class<TT> clazz, Long... ids) {
		List<IdRef<TT>> idRefs = new ArrayList<IdRef<TT>>();
		for (int i = 0; i < ids.length; i++) {
			idRefs.add(create(r, clazz, ids[i]));
		}
		return idRefs;
	}

	@SuppressWarnings("unchecked")
	public static <TT> IdRef<TT> parse(Repository r, String path) {
		String[] parts = path.split("/");

		IdRef<TT> lastIdRef = null;

		for (int i = 1; i < parts.length; i += 2) {
			String endpointPath = "/" + parts[i];
			Long asLong = Long.valueOf(parts[i + 1]);

			IdRef<TT> currentIdRef = (IdRef<TT>) create(r, r.getFeatures().get(endpointPath).getClazz(), asLong);
			currentIdRef.setParentId(lastIdRef);
			lastIdRef = currentIdRef;
		}

		return lastIdRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdRef<?> other = (IdRef<?>) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}

	@Override
	public int compareTo(IdRef<T> o) {
		return id.compareTo(o.asLong());
	}

	@Override
	public String toString() {
		return id.toString();
	}

}
