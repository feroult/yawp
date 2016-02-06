package io.yawp.servlet.parent;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.Parent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class ParentQueryTest extends ParentServletTestCase {

    @Test
    public void testQueryWithCursors() {
        for (int i = 0; i < 10; i++) {
            saveParent("xpto" + i);
        }

        String cursor = "";
        List<Parent> results = new ArrayList<>();

        for (int i = 0; i < 10; i += 2) {
            String json = get("/parents", params("q", "{ limit: 2, cursor: '" + cursor + "' }"));
            JsonObject result = new JsonParser().parse(json).getAsJsonObject();

            results.addAll(fromList(result.get("results").toString(), Parent.class));
            cursor = result.get("cursor").getAsString();
        }

        assertEquals(10, results.size());
        results.sort(new Comparator<Parent>() {
            @Override
            public int compare(Parent o1, Parent o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (int i = 0; i < results.size(); i++) {
            assertEquals("xpto" + i, results.get(i).getName());
        }
    }

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
