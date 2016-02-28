package io.yawp.tools;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Parent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteAllTest extends EndpointTestCase {

    @Test
    public void deleteAll() {
        yawp.save(new BasicObject());
        yawp.save(new Parent());

        assertEquals(1, yawp(BasicObject.class).list().size());
        assertEquals(1, yawp(Parent.class).list().size());

        DeleteAll.now();

        assertEquals(0, yawp(BasicObject.class).list().size());
        assertEquals(0, yawp(Parent.class).list().size());
    }

}
