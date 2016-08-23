package io.yawp.repository.models.basic;

import java.io.Serializable;

public class Pojo implements Serializable {

    private static final long serialVersionUID = 7597296508950375357L;

    private String stringValue;

    private Long longValue;

    public Pojo() {
    }

    public Pojo(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }
}
