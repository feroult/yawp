package io.yawp.commons.http;

import io.yawp.driver.mock.MockHttpServletResponse;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ImageResponseTest {
    @Test
    public void testImageResponseContentType() throws IOException {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        byte[] imageBytes = "image fake".getBytes();

        ImageResponse imageResponse = new ImageResponse(imageBytes);
        imageResponse.execute(httpServletResponse);

        assertEquals("image/png", httpServletResponse.getContentType());
    }
}