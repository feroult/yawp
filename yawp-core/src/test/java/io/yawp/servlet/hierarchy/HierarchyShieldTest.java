package io.yawp.servlet.hierarchy;

import io.yawp.commons.utils.ServletTestCase;
import org.junit.Test;

public class HierarchyShieldTest extends ServletTestCase {

    @Test
    public void testSuperClassAction() {
        assertPostWithStatus("/hierarchy_subclasses/1", "{ name: 'block this case' }", 404);
        assertPostWithStatus("/hierarchy_subclasses/1", "{ name: 'block this case too' }", 200);
        assertPostWithStatus("/hierarchy_another-subclasses/1", "{ name: 'block this case' }", 404);
    }

    @Test
    public void testMoreSpecificSubClassShield() {
        assertPostWithStatus("/hierarchy_another-subclasses/1", "{ name: 'block this case too' }", 404);
    }

}
