package io.yawp.repository;

import io.yawp.commons.http.HttpVerb;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.models.ObjectModel;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.servlet.cache.Cache;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class IdRef<T> implements Comparable<IdRef<T>>, Serializable {

    private static final long serialVersionUID = 2203539386465263956L;

    private transient Repository r;

    private transient Class<T> clazz;

    private transient ObjectModel model;

    private transient Long id;

    private transient String name;

    private transient IdRef<?> parentId;

    public IdRef() {
    }

    protected IdRef(Repository r, Class<T> clazz, Long id) {
        this.r = r;
        this.clazz = clazz;
        this.id = id;
        this.model = new ObjectModel(clazz);
    }

    public IdRef(Repository r, Class<T> clazz, String name) {
        this.r = r;
        this.clazz = clazz;
        this.name = name;
        this.model = new ObjectModel(clazz);
    }

    public void setParentId(IdRef<?> parentId) {
        this.parentId = parentId;
    }

    /**
     * Fetch from datastore or from request cache (if present)
     * @return the entity fetched
     */
    public T fetch() {
        return Cache.get(this);
    }

    /**
     * Force re-fetch from datastore (ignore request cache!)
     * @return the entity fetched
     */
    public T refetch() {
        return fetch(clazz);
    }

    /**
     * Fetch from datastore and cast to given childClazz (no cache is performed)
     * @param childClazz the clazz to cast to
     * @param <TT> The generic parameter of the child clazz
     * @return the fetched entity
     */
    public <TT> TT fetch(Class<TT> childClazz) {
        return r.query(childClazz).fetch(this);
    }

    public <TT> TT child(Class<TT> childClazz) {
        QueryBuilder<TT> q = r.query(childClazz).from(this);
        return q.only();
    }

    public Long asLong() {
        return id;
    }

    public String asString() {
        return name;
    }

    public Repository getRepository() {
        return r;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Class<?> getParentClazz() {
        return model.getParentClazz();
    }

    public ObjectModel getModel() {
        return new ObjectModel(clazz);
    }

    @SuppressWarnings("unchecked")
    public <TT> IdRef<TT> getParentId() {
        return (IdRef<TT>) parentId;
    }

    @SuppressWarnings("unchecked")
    public <TT> IdRef<TT> getAncestorId(int ancestor) {
        IdRef<?> ancestorId = this;
        for (int i = 0; i <= ancestor; i++) {
            ancestorId = ancestorId.getParentId();
        }
        return (IdRef<TT>) ancestorId;
    }

    public <TT> IdRef<TT> createChildId(Class<TT> clazz, Long id) {
        IdRef<TT> idRef = new IdRef<>(r, clazz, id);
        idRef.setParentId(this);
        return idRef;
    }

    public <TT> IdRef<TT> createChildId(Class<TT> clazz, String name) {
        IdRef<TT> idRef = new IdRef<>(r, clazz, name);
        idRef.setParentId(this);
        return idRef;
    }

    public <TT> IdRef<TT> createSiblingId(Class<TT> clazz) {
        if (this.name != null) {
            return new IdRef<>(r, clazz, this.name);
        }
        return new IdRef<>(r, clazz, this.id);
    }

    public <TT> IdRef<TT> of(Class<TT> clazz) {
        return (IdRef<TT>) this;
    }

    public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, Long id) {
        return new IdRef<>(r, clazz, id);
    }

    public static <TT> IdRef<TT> create(Repository r, Class<TT> clazz, String name) {
        return new IdRef<>(r, clazz, name);
    }

    public static <TT> List<IdRef<TT>> create(Repository r, Class<TT> clazz, Long... ids) {
        List<IdRef<TT>> idRefs = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            idRefs.add(create(r, clazz, ids[i]));
        }
        return idRefs;
    }

    @SuppressWarnings("unchecked")
    public static <TT> IdRef<TT> parse(Repository r, HttpVerb verb, String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }

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

            IdRef<TT> currentIdRef;

            if (!isString(parts[i + 1])) {
                Long asLong = Long.valueOf(parts[i + 1]);
                currentIdRef = (IdRef<TT>) create(r, getIdRefClazz(r, endpointPath), asLong);
            } else {
                String asString = parts[i + 1];
                currentIdRef = (IdRef<TT>) create(r, getIdRefClazz(r, endpointPath), asString);
            }

            currentIdRef.setParentId(lastIdRef);
            lastIdRef = currentIdRef;

            validateParentId(currentIdRef, path);
        }

        return lastIdRef;
    }

    public static IdRef<?> parse(Repository r, String uri) {
        return parse(r, HttpVerb.GET, uri);
    }

    public static <T> IdRef<T> parse(Class<T> clazz, Repository r, String uri) {
        return parse(r, HttpVerb.GET, uri);
    }

    public static <T> List<IdRef<T>> parse(Class<T> clazz, Repository r, List<String> uris) {
        ArrayList<IdRef<T>> ids = new ArrayList<>();
        for (String uri : uris) {
            ids.add(parse(clazz, r, uri));
        }
        return ids;
    }

    private static void validateParentId(IdRef<?> id, String path) {
        Class<?> parentClazz = id.getParentClazz();
        if (parentClazz == null) {
            return;
        }

        if (id.getParentId() == null) {
            throw new RuntimeException("Invalid parent structure for id: " + path);
        }

        if (!parentClazz.equals(id.getParentId().getClazz())) {
            throw new RuntimeException("Invalid parent structure for id: " + path);
        }
    }

    private static Class<?> getIdRefClazz(Repository r, String endpointPath) {
        return r.getFeatures().getByPath(endpointPath).getClazz();
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

    public Object getSimpleValue() {
        if (id != null) {
            return id;
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public boolean isShuffled() {
        return model.isIdShuffled();
    }

    public boolean isAncestorId(IdRef<?> childId) {
        IdRef<?> currentId = childId;
        while (currentId.getParentId() != null) {
            if (currentId.getParentId().getClazz().equals(this.getClazz())) {
                return this.equals(currentId.getParentId());
            }
            currentId = currentId.getParentId();
        }
        return false;
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
        if (id != null && o.id != null) {
            return id.compareTo(o.id);
        }
        return idOrNameAsString().compareTo(o.idOrNameAsString());
    }

    private String idOrNameAsString() {
        return id != null ? String.valueOf(id) : name;
    }

    @Override
    public String toString() {
        if (r == null) {
            return "empty id";
        }
        return getUri();
    }

    public String getUri() {
        StringBuilder sb = new StringBuilder();
        if (parentId != null) {
            sb.append(parentId.toString());
        }
        sb.append(r.getFeatures().getByClazz(clazz).getEndpointPath());
        sb.append("/");
        sb.append(id != null ? id : name);
        return sb.toString();
    }

    public boolean isSibling(IdRef<?> otherId) {
        if (this.name != null) {
            return otherId.name != null && this.name.equals(otherId.name);
        }
        if (this.id != null) {
            return otherId.id != null && this.id.equals(otherId.id);
        }
        return otherId.name == null && otherId.id == null;
    }

    // Serializable
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(getUri());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String uri = (String) in.readObject();

        IdRef<T> idRef = (IdRef<T>) parse(yawp(), uri);
        this.r = idRef.r;
        this.clazz = idRef.clazz;
        this.model = idRef.model;
        this.id = idRef.id;
        this.name = idRef.name;
        this.parentId = idRef.parentId;
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

}
