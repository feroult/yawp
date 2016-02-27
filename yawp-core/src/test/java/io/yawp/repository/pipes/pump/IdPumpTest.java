package io.yawp.repository.pipes.pump;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static org.junit.Assert.*;

public class IdPumpTest extends PumpTestCase<IdRef<BasicObject>> {
    @Test
    public void testBasicList() {
        List<IdRef<BasicObject>> list = getIds(saveManyBasicObjects(3));

        IdPump<BasicObject> pump = new IdPump<>(BasicObject.class, 2);

        pumpTestBasicList(pump, list);
    }

    @Test
    public void testBasicQuery() {
        saveManyBasicObjects(3);

        IdPump<BasicObject> pump = new IdPump<>(BasicObject.class, 2);

        pumpTestBasicQuery(pump);
    }

    @Test
    public void testMultipleQueries() {
        saveManyBasicObjects(11);

        IdPump<BasicObject> pump = new IdPump<>(BasicObject.class, 2);

        pumpTestMultipleQueries(pump);
    }

    @Test
    public void testListAndMultipleQueries() {
        saveManyBasicObjects(11);
        List<IdRef<BasicObject>> list = getIds(saveManyBasicObjects(5, 11));

        IdPump<BasicObject> pump = new IdPump<>(BasicObject.class, 2);

        pumpTestListAndMultipleQueries(pump, list);
    }

    private List<IdRef<BasicObject>> getIds(List<BasicObject> objects) {
        List<IdRef<BasicObject>> ids = new ArrayList<>();
        for (BasicObject object : objects) {
            ids.add(object.getId());
        }
        return ids;
    }

    @Override
    protected void assertList(List<IdRef<BasicObject>> list, int... values) {
        assertEquals(values.length, list.size());
        for (int i = 0; i < values.length; i++) {
            BasicObject object = list.get(i).fetch();
            assertEquals(values[i], object.getIntValue());
        }
    }

}
