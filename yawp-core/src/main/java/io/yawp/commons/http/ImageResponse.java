package io.yawp.commons.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ImageResponse extends HttpResponse {

    private byte[] imageBytes;

    public ImageResponse(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void execute(HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        resp.getOutputStream().write(imageBytes);
    }
}
