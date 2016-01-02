package io.yawp.repository;

import java.util.HashSet;
import java.util.Set;

public class FeatureTree<T extends Feature> {

    private Class<T> stopclazz;

    private Set<Class<? extends T>> nodes = new HashSet<>();

    private Set<Class<? extends T>> leafs = new HashSet<>();

    public FeatureTree(Class<T> stopclazz) {
        this.stopclazz = stopclazz;
    }

    public void add(Class<? extends T> clazz) {
        if (!nodes.contains(clazz)) {
            addNode(clazz);
            addLeaf(clazz);
        }
        addHierarchyNodes(clazz);
    }

    private void addHierarchyNodes(Class<? extends T> clazz) {
        Class<? extends T> superclazz = (Class<? extends T>) clazz.getSuperclass();

        if (superclazz == null || superclazz.equals(stopclazz)) {
            return;
        }

        if (nodes.contains(superclazz)) {
            leafs.remove(superclazz);
            return;
        }

        addNode(superclazz);
    }

    private void addLeaf(Class<? extends T> clazz) {
        leafs.add(clazz);
    }

    private void addNode(Class<? extends T> clazz) {
        nodes.add(clazz);
    }

    public Set<Class<? extends T>> getLeafs() {
        return leafs;
    }

    public int size() {
        return nodes.size();
    }
}
