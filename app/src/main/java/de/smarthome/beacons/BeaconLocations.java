package de.smarthome.beacons;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class BeaconLocations {
    private static final String TAG = "BeaconLocations";
    private final List<BeaconLocation> beaconLocationList;

    public BeaconLocations(@JsonProperty("locations") List<BeaconLocation> beaconLocationList) {
        this.beaconLocationList = beaconLocationList;
    }

    public Optional<String> getRoomUID(BeaconID beaconID) {
        for(BeaconLocation location : beaconLocationList) {
            if(location.getBeaconId().equals(beaconID.getId())) {
                return Optional.of(location.getRoomUID());
            }
        }
        Log.d(TAG, "unknown beaconId" + beaconID);

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "BeaconLocations{" +
                "beaconLocationList=" + beaconLocationList +
                '}';
    }
}
