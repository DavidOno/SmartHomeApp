package de.smarthome.app.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is capable of storing all information given as response from GIRA when
 * checking its availability.
 */
public class AvailabilityResponse {

    private final String info;
    private final String deviceName;
    private final String version;
    private final String deviceType;
    private final String deviceVersion;

    public AvailabilityResponse(@JsonProperty("info") String info, @JsonProperty("deviceName") String deviceName, @JsonProperty("version") String version, @JsonProperty("deviceType") String deviceType, @JsonProperty("deviceVersion") String deviceVersion) {
        this.info = info;
        this.deviceName = deviceName;
        this.version = version;
        this.deviceType = deviceType;
        this.deviceVersion = deviceVersion;
    }

    public String getInfo() {
        return info;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getVersion() {
        return version;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "\ninfo='" + info + '\'' +
                "\n, deviceName='" + deviceName + '\'' +
                "\n, version='" + version + '\'' +
                "\n, deviceType='" + deviceType + '\'' +
                "\n, deviceVersion='" + deviceVersion + '\'' +
                "\n}";
    }
}
