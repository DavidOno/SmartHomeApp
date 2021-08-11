package de.smarthome.beacons;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * List of locations
 */
public class BeaconLocations {
    private static final String TAG = "BeaconLocations";
    private final List<BeaconLocation> beaconLocationList;

    public BeaconLocations(@JsonProperty("locations") List<BeaconLocation> beaconLocationList) {
        this.beaconLocationList = beaconLocationList;
    }

    /**
     * Based on a beaconID a roomID can be extracted out of a list of locations.
     * @param beaconID Unique id of the beacon. Used to check a list of beacons for occurrence.
     * @return The associated roomUID for a beaconID. If empty, an empty Object of the type
     * optional is returned.
     */
    public Optional<String> getRoomUID(BeaconID beaconID) {
        for(BeaconLocation location : beaconLocationList) {
            if(location.getBeaconId().equals(beaconID.getId())) {
                return Optional.of(location.getRoomUID());
            }
        }
        Log.d(TAG, "unknown beaconId" + beaconID);

        return Optional.empty();
    }

    /**
     * Checks whether the given beaconID is registered inside this config.
     * @param beaconID BeaconID to be checked
     * @return true if the beaconID is registered, false otherwise.
     */
    public boolean isRegistered(BeaconID beaconID){
        return beaconLocationList.stream().anyMatch(beaconLocation -> beaconLocation.getBeaconId().equals(beaconID));
    }

    @Override
    public String toString() {
        return "BeaconLocations{" +
                "beaconLocationList=" + beaconLocationList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeaconLocations that = (BeaconLocations) o;
        return Objects.equals(beaconLocationList, that.beaconLocationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beaconLocationList);
    }
}
