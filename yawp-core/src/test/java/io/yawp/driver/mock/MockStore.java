package io.yawp.driver.mock;

import io.yawp.driver.mock.MockOperation.Type;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.LRUMap;

public class MockStore {

    private static Map<NamespacedIdRef, Object> store = new LinkedHashMap<>();

    private static long nextId = 1;

    private static LRUMap cursors = new LRUMap(3);

    private static ThreadLocal<String> namespace = new ThreadLocal<String>();

    private static LRUMap transactions = new LRUMap(3);

    public static void put(IdRef<?> id, Object object, String tx) {
        Object clone = cloneBean(object);

        transactionLog(tx, MockOperation.Type.PUT, id, clone, get(id));
        store.put(createNamespacedId(id), clone);
    }

    public static Object get(IdRef<?> id) {
        try {
            Object bean = store.get(createNamespacedId(id));
            if (bean == null) {
                return null;
            }
            return BeanUtils.cloneBean(bean);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void remove(IdRef<?> id, String tx) {
        transactionLog(tx, MockOperation.Type.REMOVE, id, null, get(id));
        store.remove(createNamespacedId(id));
    }

    public static List<Object> list(Class<?> clazz, IdRef<?> parentId) {
        List<Object> objects = new ArrayList<Object>();

        for (NamespacedIdRef namespacedId : store.keySet()) {

            if (!namespacedId.isFrom(getNamespace())) {
                continue;
            }

            ObjectHolder objectHolder = new ObjectHolder(store.get(namespacedId));

            IdRef<?> id = objectHolder.getId();

            if (!id.getClazz().equals(clazz)) {
                continue;
            }

            if (!isAncestor(id, parentId)) {
                continue;
            }

            objects.add(get(id));
        }

        return objects;
    }

    private static boolean isAncestor(IdRef<?> id, IdRef<?> parentId) {
        if (parentId == null) {
            return true;
        }

        IdRef<?> currentParentId = id.getParentId();
        while (currentParentId != null) {
            if (currentParentId.equals(parentId)) {
                return true;
            }
            currentParentId = currentParentId.getParentId();
        }
        return false;
    }

    public static long nextId() {
        return nextId++;
    }

    public static void clear() {
        nextId = 1;
        store.clear();
    }

    public static void clear(String namespace) {
        store.remove(namespace);
    }

    public static List<String> namespaces() {
        List<String> nss = new ArrayList<>();
        for (NamespacedIdRef ns : store.keySet()) {
            nss.add(ns.getNamespace());
        }
        return nss;
    }

    public static String createCursor(Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);
        String cursor = UUID.randomUUID().toString();
        cursors.put(cursor, objectHolder.getId());
        return cursor;
    }

    public static IdRef<?> getCursor(String cursor) {
        return (IdRef<?>) cursors.get(cursor);
    }

    public static void updateCursor(String cursor, Object object) {
        ObjectHolder objectHolder = new ObjectHolder(object);
        cursors.put(cursor, objectHolder.getId());
    }

    public static void setNamespace(String ns) {
        namespace.set(ns);
    }

    public static String getNamespace() {
        return namespace.get();
    }

    public static String createTransaction() {
        String tx = UUID.randomUUID().toString();
        transactions.put(tx, new MockTransaction());
        return tx;
    }

    public static void rollback(String tx) {
        MockTransaction mockTransaction = (MockTransaction) transactions.get(tx);
        mockTransaction.rollback();
        transactions.remove(tx);
    }

    public static void commit(String tx) {
        // till now we dont need to mock transaction isolation
        transactions.remove(tx);
    }

    private static Object cloneBean(Object object) {
        try {
            return BeanUtils.cloneBean(object);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static NamespacedIdRef createNamespacedId(IdRef<?> id) {
        return new NamespacedIdRef(getNamespace(), id);
    }

    private static void transactionLog(String tx, Type operationType, IdRef<?> id, Object object, Object previousObject) {
        if (tx == null) {
            return;
        }

        MockTransaction mockTransaction = (MockTransaction) transactions.get(tx);
        mockTransaction.add(operationType, id, object, previousObject);
    }

}
