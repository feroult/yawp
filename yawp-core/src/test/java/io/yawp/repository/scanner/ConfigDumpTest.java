package io.yawp.repository.scanner;

import io.yawp.repository.RepositoryFeatures;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigDumpTest {

    @Test
    public void testSimple() {
        RepositoryScanner scanner = new RepositoryScanner("io.yawp.repository.models.parents");
        RepositoryFeatures feature = scanner.scan();

        assertTrue(true);
    }

}
