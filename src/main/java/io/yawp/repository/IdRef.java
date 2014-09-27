package io.yawp.repository;

import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.query.DatastoreQuery;
import io.yawp.utils.EntityUtils;
import io.yawp.utils.HttpVerb;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class IdRef<T> implements Comparable<IdRef<T>> {

	private Repository r;

	private Class<T> clazz;

	private Long id;

	private String name;

	private IdRef<?> parentId;

	protected IdRef(Repository r, Class<T> clazz, Long id) {
		this.r = r;
		this.clazz = clazz;
		this.id = id;
	}

	public IdRef(Repository r, Class<T> clazz, String name) {
		this.r = r;
		this.clazz = clazz;
		this.name = name;
	}

	public void setParentId(IdRef<?> parentId) {
		this.parentId = parentId;
	}

	public T fetch() {
		return fetch(clazz);
	}

	public <TT> TT fetch(Class<TT> childClazz) {
		return r.query(childClazz).fetch(this);
	}

	public <TT> TT child(Class<TT> childClazz) {
		DatastoreQuery<TT> q = r.query(childClazz).from(this);
		return q.only();
	}

	public Long asLong() {
		return id;
	}

	public String asString() {
		return name;
	}

	public Key asKey() {
		Key parent = parentId == null ? null : parentId.asKey();
		String kind = EntityUtils.getKindFromClass(clazz);
		if (id == null) {
			return KeyFactory.createKey(parent, kind, name);
		}
		return KeyFactory.createKey(parent, kind, id);
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
		Class<?> objectClass = EntityUtils.getClassFromKind(r, key.getKind());

		IdRef<?> ref = null;
		if (key.getName() != null) {
			ref = IdRef.create(r, EntityUtils.getIdType(objectClass), key.getName());
		} else {
			ref = IdRef.create(r, EntityUtils.getIdType(objectClass), key.getId());
		}
		ref.parentId = fromKey(r, key.getParent());
		return ref;
	}

	public <TT> IdRef<TT> createChildId(Class<TT> clazz, Long id) {
		IdRef<TT> idRef = new IdRef<TT>(r, clazz, id);
		idRef.setParentId(this);
		return idRef;
	}

	public <TT> IdRef<TT> createChildId(Class<TT> clazz, String name) {
		IdRef<TT> idRef = new IdRef<TT>(r, clazz, name);
		idRef.setParentId(this);
		return idRef;
	}

	public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, Long id) {
		return new IdRef<TT>(r, clazz, id);
	}

	public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, String name) {
		return new IdRef<TT>(r, clazz, name);
	}

	public static <TT> List<IdRef<TT>> create(Repository r, Class<TT> clazz, Long... ids) {
		List<IdRef<TT>> idRefs = new ArrayList<IdRef<TT>>();
		for (int i = 0; i < ids.length; i++) {
			idRefs.add(create(r, clazz, ids[i]));
		}
		return idRefs;
	}

	@SuppressWarnings("unchecked")
	public static <TT> IdRef<TT> parse(Repository r, HttpVerb verb, String path) {
		String[] parts = path.substring(1).split("/");

		if (parts.length < 2) {
			return null;
		}

		IdRef<TT> lastIdRef = null;

		for (int i = 0; i < parts.length; i += 2) {
			if (isActionOrCollection(r, verb, parts, i)) {
				break;
			}

			String endpointPath = "/" + parts[i];

			IdRef<TT> currentIdRef = null;

			if (!isString(parts[i + 1])) {
				Long asLong = Long.valueOf(parts[i + 1]);
				currentIdRef = (IdRef<TT>) create(r, getIdRefClazz(r, endpointPath), asLong);
			} else {
				String asString = parts[i + 1];
				currentIdRef = (IdRef<TT>) create(r, getIdRefClazz(r, endpointPath), asString);
			}

			currentIdRef.setParentId(lastIdRef);
			lastIdRef = currentIdRef;
		}

		return lastIdRef;
	}

	private static Class<?> getIdRefClazz(Repository r, String endpointPath) {
		return r.getFeatures().get(endpointPath).getClazz();
	}

	private static boolean isActionOrCollection(Repository r, HttpVerb verb, String[] parts, int i) {
		if (i < parts.length - 2) {
			return false;
		}

		if (i == parts.length - 1) {
			return true;
		}

		if (!isString(parts[parts.length - 1])) {
			return false;
		}

		String endpointPath = "/" + parts[parts.length - 2];
		String possibleAction = parts[parts.length - 1];

		ActionKey actionKey = new ActionKey(verb, possibleAction, true);
		return r.getFeatures().hasCustomAction(endpointPath, actionKey);
	}

	private static boolean isString(String s) {
		try {
			Long.valueOf(s);
			return false;
		} catch (NumberFormatException e) {
			return true;
		}
	}

	public void delete() {
		r.destroy(this);
	}

	public List<IdRef<?>> children() {
		List<IdRef<?>> ids = new ArrayList<>();
		for (EndpointFeatures<?> childEndpoint : r.getFeatures().getChildren(clazz)) {
			ids.addAll(r.query(childEndpoint.getClazz()).from(this).ids());
		}
		return ids;
	}

	public Object getSimpleValue() {
		if (id != null) {
			return id;
		}
		return name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		return getUri();
	}

	public String getUri() {
		StringBuilder sb = new StringBuilder();
		if (parentId != null) {
			sb.append(parentId.toString());
		}
		sb.append(r.getFeatures().get(clazz).getEndpointPath());
		sb.append("/");
		sb.append(id != null ? id : name);
		return sb.toString();
	}
}
