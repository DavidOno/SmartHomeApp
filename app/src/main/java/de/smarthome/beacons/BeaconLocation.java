package de.smarthome.beacons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeaconLocation {
    private final String roomUID;
    private final String beaconId;

    public BeaconLocation(@JsonProperty("roomUID") String roomUID, @JsonProperty("beaconId") String beaconId) {
        this.roomUID = roomUID;
        this.beaconId = beaconId;
    }

    public String getRoomUID() {
        return roomUID;
    }

    public String getBeaconId() {
        return beaconId;
    }

    @Override
    public String toString() {
        return "BeaconLocation{" +
                "roomUID='" + roomUID + '\'' +
                ", beaconId='" + beaconId + '\'' +
                '}';
    }
}
