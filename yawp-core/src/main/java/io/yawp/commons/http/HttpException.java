package io.yawp.commons.http;

import io.yawp.commons.utils.Environment;

import com.google.gson.JsonElement;

public class HttpException extends RuntimeException {

    private static final long serialVersionUID = -1369195874459839005L;

    private int httpStatus;

    private String text;

    public HttpException(int httpStatus, String text) {
        this.httpStatus = httpStatus;
        this.text = text;
    }

    public HttpException(int httpStatus) {
        this(httpStatus, (String) null);
    }

    public HttpException(int httpStatus, JsonElement json) {
        this(httpStatus, json.toString());
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getText() {
        return text;
    }

    public HttpResponse createErrorResponse() {
        if (getHttpStatus() == 404 && Environment.isProduction()) {
            return new ErrorResponse(getHttpStatus(), "Not Found");
        }

        return new ErrorResponse(getHttpStatus(), getText());
    }

    @Override
    public String toString() {
        return "<HttpException status:" + this.httpStatus + (text == null ? "" : " text:'" + text + "'") + ">";
    }
}
