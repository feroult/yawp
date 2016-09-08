package io.yawp.commons.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpVerbTest {
    @Test
    public void testConversionFromStringUsingGet() {
        assertEquals(HttpVerb.GET, HttpVerb.fromString("get"));
    }

    @Test
    public void testConversionFromStringUsingPost() {
        assertEquals(HttpVerb.POST, HttpVerb.fromString("post"));
    }

    @Test
    public void testConversionFromStringUsingPut() {
        assertEquals(HttpVerb.PUT, HttpVerb.fromString("put"));
    }

    @Test
    public void testConversionFromStringUsingPatch() {
        assertEquals(HttpVerb.PATCH, HttpVerb.fromString("patch"));
    }

    @Test
    public void testConversionFromStringUsingDelete() {
        assertEquals(HttpVerb.DELETE, HttpVerb.fromString("delete"));
    }

    @Test
    public void testConversionFromStringUsingOptions() {
        assertEquals(HttpVerb.OPTIONS, HttpVerb.fromString("options"));
    }
}