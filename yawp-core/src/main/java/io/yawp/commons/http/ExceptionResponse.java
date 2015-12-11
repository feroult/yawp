package io.yawp.commons.http;

import io.yawp.commons.utils.Environment;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
        resp.setStatus(httpStatus);

        String userText = getUserText();

        if (userText == null) {
            return;
        }

        if (httpStatus >= 200 && httpStatus < 300) {
            resp.getWriter().write(StatusObject.success(userText).toJson());
        } else {
            resp.getWriter().write(StatusObject.error(userText).toJson());
        }
    }

    private String getUserText() {
        String userText = text;

        if (httpStatus == 404 && Environment.isProduction()) {
            userText = "Not Found";
        }
        return userText;
    }
}
