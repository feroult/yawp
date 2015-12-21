package io.yawp.commons.http;

import io.yawp.commons.utils.Environment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionResponse extends HttpResponse {

    private int httpStatus;
    private String text;

    public ExceptionResponse(int httpStatus, String text) {
        this.httpStatus = httpStatus;
        this.text = text;
    }

    public ExceptionResponse(int httpStatus) {
        this(httpStatus, null);
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void execute(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(httpStatus);

        String json = getJson();
        if (json == null) {
            return;
        }

        resp.getWriter().write(json);
    }

    private String getJson() {
        if (httpStatus == 404 && Environment.isProduction()) {
            return "{}";
        }
        return text;
    }
}
