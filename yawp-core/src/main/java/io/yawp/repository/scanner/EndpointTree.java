package io.yawp.repository.scanner;

import io.yawp.repository.hooks.Hook;

import java.util.Set;

public class EndpointTree<T> {

    private FeatureTree<Hook> hookTree = new FeatureTree<>(Hook.class);

    public void addHook(Class<? extends Hook> hookClazz) {
        hookTree.add(hookClazz);
    }

    public Set<Class<? extends Hook>> loadHooks() {
        return hookTree.getLeafs();
    }
}
