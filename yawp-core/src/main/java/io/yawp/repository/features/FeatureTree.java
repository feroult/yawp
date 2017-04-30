package io.yawp.repository.features;

import io.yawp.repository.Feature;

import java.util.HashSet;
import java.util.Set;

public class FeatureTree<T extends Feature> {

    private Class<T> stopclazz;

    private Set<Class<? extends T>> nodes = new HashSet<>();

    private Set<Class<? extends T>> leaves = new HashSet<>();

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
            leaves.remove(superclazz);
            return;
        }

        addNode(superclazz);
    }

    private void addLeaf(Class<? extends T> clazz) {
        leaves.add(clazz);
    }

    private void addNode(Class<? extends T> clazz) {
        nodes.add(clazz);
    }

    public Set<Class<? extends T>> getLeaves() {
        return leaves;
    }

    public int size() {
        return nodes.size();
    }
}
