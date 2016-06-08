package io.yawp.commons.http;

import com.google.gson.JsonElement;

public class HttpException extends RuntimeException {

    private static final long serialVersionUID = -1369195874459839005L;

    private final int httpStatus;

    private final String text;

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

    public HttpResponse createResponse() {
        return new ExceptionResponse(getHttpStatus(), getText());
    }

    @Override
    public String toString() {
        return "<HttpException status:" + this.httpStatus + (text == null ? "" : " text:'" + text + "'") + ">";
    }
}
