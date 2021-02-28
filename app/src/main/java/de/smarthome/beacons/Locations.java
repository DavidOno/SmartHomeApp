package de.smarthome.beacons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Locations {
    private List<RoomLocation> locationsList = new ArrayList<>();

    public Locations(@JsonProperty("locations") List<RoomLocation> locationsList) {
        this.locationsList = locationsList;
    }
}
