package io.yawp.commons.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NameGeneratorTest {

    @Test
    public void testGenerateAndConvertString() {
        assertEquals("xpto", NameGenerator.convertToString(NameGenerator.generateFromString("xpto")));
        assertEquals("lala 123", NameGenerator.convertToString(NameGenerator.generateFromString("lala 123")));
    }
}
