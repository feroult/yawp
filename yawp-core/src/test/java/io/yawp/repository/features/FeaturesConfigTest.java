package io.yawp.repository.features;

import io.yawp.repository.models.parents.Parent;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FeaturesConfigTest {

    @Test
    @Ignore
    public void testSimple() {
        FeaturesConfig config = new FeaturesConfig("yawp.features.test.yml");
        Features features = config.load();

        assertEquals(Parent.class, features.getByPath("/parents"));
    }

}
