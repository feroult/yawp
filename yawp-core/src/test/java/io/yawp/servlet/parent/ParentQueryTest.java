package io.yawp.servlet.parent;

import static org.junit.Assert.assertEquals;

import io.yawp.repository.models.parents.Parent;

import java.util.List;

import org.junit.Test;

public class ParentQueryTest extends ParentServletTestCase {

    @Test
    public void testQuery() {
        saveParent("xpto1");
        saveParent("xpto2");

        String json = get("/parents", params("q", "{ where: ['name', '=', 'xpto1' ] }"));
        List<Parent> parents = fromList(json, Parent.class);

        assertEquals(1, parents.size());
        assertEquals("xpto1", parents.get(0).getName());
    }

}
