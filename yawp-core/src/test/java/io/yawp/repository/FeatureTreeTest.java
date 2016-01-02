package io.yawp.repository;

import io.yawp.repository.hooks.Hook;
import io.yawp.repository.hooks.hierarchy.AbstractHook;
import io.yawp.repository.hooks.hierarchy.AllObjectsHook;
import io.yawp.repository.hooks.hierarchy.ObjectSuperClassHook;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureTreeTest {

    @Test
    public void testOneLeaf() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(ObjectSuperClassHook.class);

        Set<Class<? extends Hook>> leafs = tree.getLeafs();

        assertEquals(2, tree.size());
        assertEquals(1, leafs.size());
        assertTrue(leafs.contains(ObjectSuperClassHook.class));
    }

    @Test
    public void testTwoLeafs() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(ObjectSuperClassHook.class);
        tree.add(AllObjectsHook.class);

        Set<Class<? extends Hook>> leafs = tree.getLeafs();

        assertEquals(3, tree.size());
        assertEquals(2, leafs.size());
        assertTrue(leafs.contains(ObjectSuperClassHook.class));
        assertTrue(leafs.contains(AllObjectsHook.class));
    }

    @Test
    public void testOneLeafsWithHierarchyBottomUp() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(ObjectSuperClassHook.class);
        tree.add(AbstractHook.class);

        Set<Class<? extends Hook>> leafs = tree.getLeafs();

        assertEquals(2, tree.size());
        assertEquals(1, leafs.size());
        assertTrue(leafs.contains(ObjectSuperClassHook.class));
    }

    @Test
    public void testOneLeafsWithHierarchyUpBottom() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(AbstractHook.class);
        tree.add(ObjectSuperClassHook.class);

        Set<Class<? extends Hook>> leafs = tree.getLeafs();

        assertEquals(2, tree.size());
        assertEquals(1, leafs.size());
        assertTrue(leafs.contains(ObjectSuperClassHook.class));
    }

}
