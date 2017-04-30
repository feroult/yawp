package io.yawp.repository.features;

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

        Set<Class<? extends Hook>> leaves = tree.getLeaves();

        assertEquals(2, tree.size());
        assertEquals(1, leaves.size());
        assertTrue(leaves.contains(ObjectSuperClassHook.class));
    }

    @Test
    public void testTwoLeaves() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(ObjectSuperClassHook.class);
        tree.add(AllObjectsHook.class);

        Set<Class<? extends Hook>> leaves = tree.getLeaves();

        assertEquals(3, tree.size());
        assertEquals(2, leaves.size());
        assertTrue(leaves.contains(ObjectSuperClassHook.class));
        assertTrue(leaves.contains(AllObjectsHook.class));
    }

    @Test
    public void testOneLeafWithHierarchyBottomUp() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(ObjectSuperClassHook.class);
        tree.add(AbstractHook.class);

        Set<Class<? extends Hook>> leaves = tree.getLeaves();

        assertEquals(2, tree.size());
        assertEquals(1, leaves.size());
        assertTrue(leaves.contains(ObjectSuperClassHook.class));
    }

    @Test
    public void testOneLeafWithHierarchyUpBottom() {
        FeatureTree<Hook> tree = new FeatureTree<>(Hook.class);
        tree.add(AbstractHook.class);
        tree.add(ObjectSuperClassHook.class);

        Set<Class<? extends Hook>> leaves = tree.getLeaves();

        assertEquals(2, tree.size());
        assertEquals(1, leaves.size());
        assertTrue(leaves.contains(ObjectSuperClassHook.class));
    }

}
