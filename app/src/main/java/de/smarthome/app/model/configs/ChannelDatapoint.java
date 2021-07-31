package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a GIRA-channeldatapoint.
 */
public class ChannelDatapoint {
    private final String name;
    private final DatapointType type;
    private final DatapointAccess access;

    public ChannelDatapoint(@JsonProperty("name") String name, @JsonProperty("typ") String type, @JsonProperty("access") String access) {
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

    @Override
    public String toString() {
        return "ChannelDatapoint{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", access=" + access +
                '}';
    }
}
