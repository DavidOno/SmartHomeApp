package de.smarthome.beacons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class BeaconLocations {
    private final List<BeaconLocation> beaconLocationList;

    public BeaconLocations(@JsonProperty("locations") List<BeaconLocation> beaconLocationList) {
        this.beaconLocationList = beaconLocationList;
    }

//    public Optional<String> getRoomUID(BeaconID beaconID) {
//        for(BeaconLocation location : beaconLocationList) {
//            if(location.getBeaconId().equals(beaconID.getId())) {
//                return Optional.of(location.getRoomUID());
//            }
//        }
//        System.out.println("BeaconID unknown: " + beaconID);
//
//        return Optional.empty();
//    }
}
