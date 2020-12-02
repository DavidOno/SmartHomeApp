package de.smarthome.model;

import java.util.List;

public interface Location {

    List<Location> getLocations();
    String getName();
    String getID();
    String getType();
    List<String> getFunctionsID();
}
