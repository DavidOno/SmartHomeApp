package de.smarthome.beacons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomLocation {
    private final String roomId;
    private final String beaconId;

    RoomLocation(@JsonProperty("roomUID") String roomId, @JsonProperty("beaconId") String beaconId) {
        this.roomId = roomId;
        this.beaconId = beaconId;
    }

    String getRoomId() {
        return roomId;
    }

    String getRoomName() {
        return beaconId;
    }
}
