package de.smarthome.beacons;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class creates a unique location by combining the ids of a room and the associated beacon
 */
public class BeaconLocation {
    private final String roomUID;
    private final String beaconId;

    /**
     * By combining the roomUID and the beaconUID this constructor creates a unique object for
     * every room.
     * The information are extracted out of a json file which is send by the GIRA server.
     * @param roomUID Unique id for the room which is represented by the beacon
     * @param beaconId Unique
     */
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
