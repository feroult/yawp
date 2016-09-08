package io.yawp.commons.http;

import io.yawp.driver.mock.MockHttpServletResponse;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JsonResponseTest {
    @Test
    public void testJsonResponseContentType() throws IOException {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        String json = null;

        JsonResponse jsonResponse = new JsonResponse(json);
        jsonResponse.execute(httpServletResponse);

        assertEquals("application/json;charset=UTF-8", httpServletResponse.getContentType());
    }

    @Test
    public void testJsonResponseCharacterEncoding() throws IOException {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        String json = null;

        JsonResponse jsonResponse = new JsonResponse(json);
        jsonResponse.execute(httpServletResponse);

        assertEquals("UTF-8", httpServletResponse.getCharacterEncoding());
    }
}
