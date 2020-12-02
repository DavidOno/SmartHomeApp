package de.smarthome.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

import de.smarthome.model.Location;

public class LocationImpl implements Location {

    private final String name;
    private final String ID;
    private final String type;
    private final List<String> functionsID;
    private final List<Location> locations;

    public LocationImpl(@JsonProperty("displayName") String name,
                        @JsonProperty("uid") String ID,
                        @JsonProperty("$type") String type,
                        @JsonProperty("functions") List<String> functionsID,
                        @JsonProperty("locations") List<Location> locations) {
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.functionsID = Collections.unmodifiableList(functionsID);
        this.locations = Collections.unmodifiableList(locations);
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<String> getFunctionsID() {
        return functionsID;
    }
}
