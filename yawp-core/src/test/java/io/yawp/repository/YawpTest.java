package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class YawpTest extends EndpointTestCase {

    @Test
    public void testSimpleRepositoryAccess() {
        Yawp.yawp.save(new BasicObject("xpto"));
        BasicObject retrievedObject = Yawp.yawp(BasicObject.class).first();
        assertEquals("xpto", retrievedObject.getStringValue());
    }

    @Test
    public void testFetchAll() {
        IdRef<BasicObject> id1 = Yawp.yawp.save(new BasicObject("xp1")).getId();
        IdRef<BasicObject> id2 = Yawp.yawp.save(new BasicObject("xp2")).getId();
        IdRef<BasicObject> id3 = Yawp.yawp.save(new BasicObject("xp3")).getId();

        Collection<BasicObject> els = Yawp.yawp.driver().query().fetchAll(Arrays.asList(id1, id2, id3)).values();
        String names = els.stream().map(BasicObject::getStringValue).sorted().collect(Collectors.joining(", "));
        assertEquals("xp1, xp2, xp3", names);

    }
}
