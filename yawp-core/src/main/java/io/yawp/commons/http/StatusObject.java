package io.yawp.commons.http;

import io.yawp.commons.utils.JsonUtils;

import com.google.gson.annotations.SerializedName;

public class StatusObject {

    public enum Status {
        @SerializedName("success")
        SUCCESS, @SerializedName("fail")
        FAIL, @SerializedName("error")
        ERROR;
    }

    private Status status;

    private String message;

    private StatusObject(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static StatusObject success(String message) {
        return new StatusObject(Status.SUCCESS, message);
    }

    public static StatusObject success() {
        return new StatusObject(Status.SUCCESS, null);
    }

    public static StatusObject fail(String message) {
        return new StatusObject(Status.FAIL, message);
    }

    public static StatusObject error(String message) {
        return new StatusObject(Status.ERROR, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {
        return JsonUtils.to(this);
    }

}
