package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.cache.Cache;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Assert;
import org.junit.Test;

public class CacheTest extends EndpointTestCase {

    @Test
    public void testCacheMechanism() {
        BasicObject bo = yawp.save(new BasicObject("test"));
        IdRef<BasicObject> id = bo.getId();
        Assert.assertEquals("test", Cache.get(id).getStringValue());

        bo.setStringValue("changed");
        yawp.save(bo);
        Assert.assertEquals("test", Cache.get(id).getStringValue());

        Cache.clearAll();
        Assert.assertEquals("changed", Cache.get(id).getStringValue());
    }
}