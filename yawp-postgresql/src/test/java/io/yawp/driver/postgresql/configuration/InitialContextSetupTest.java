package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.ResourceFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class InitialContextSetupTest {

    @Before
    public void setup() {
        Configuration.setEnv("test");
    }

    @After
    public void tearDown() {
        InitialContextSetup.unregister();
    }

    @Test
    public void testSetupWithResource() throws NamingException {
        InitialContextSetup.configure("configuration/jetty-env-test.xml");
        assertInitialContextHasDataSource();
    }

    @Test
    public void testSetupWithFile() throws NamingException, IOException {
        InitialContextSetup.configure(getFile());
        assertInitialContextHasDataSource();
    }

    private void assertInitialContextHasDataSource() throws NamingException {
        Context ctx = (Context) new InitialContext().lookup("java:comp/env");
        DataSource ds = (DataSource) ctx.lookup(Configuration.envDataSourceName());

        assertNotNull(ds);
    }

    private File getFile() throws IOException {
        String path = new ResourceFinder().find("configuration/jetty-env-test.xml").getFile();
        return new File(path);
    }

}
