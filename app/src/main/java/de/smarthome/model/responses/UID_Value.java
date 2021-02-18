package de.smarthome.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UID_Value {

    private final String uid;
    private final String value;

    public UID_Value(@JsonProperty("uid") String uid, @JsonProperty("value") String value) {
        this.uid = uid;
        this.value = value;
    }

    public String getUid() {
        return uid;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Value{" +
                "\nuid='" + uid + '\'' +
                "\n, value='" + value + '\'' +
                "\n}";
    }
}
