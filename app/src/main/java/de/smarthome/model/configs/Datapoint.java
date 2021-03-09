package de.smarthome.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Datapoint {
    private final String name;
    private final DatapointType type;
    private final DatapointAccess access;

    public Datapoint(@JsonProperty("name") String name, @JsonProperty("typ") String type, @JsonProperty("access") String access) {
        this.name = name;
        this.type = DatapointType.convert(type);
        this.access = DatapointAccess.convert(access);
    }

    public String getName() {
        return name;
    }

    public DatapointType getType() {
        return type;
    }

    public DatapointAccess getAccess() {
        return access;
    }
}
