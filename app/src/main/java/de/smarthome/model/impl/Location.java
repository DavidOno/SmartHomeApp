package de.smarthome.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Location {

    private final String name;
    private final String ID;
    private final String type;
    private final String locationType;
    private final List<String> functionsID;
    private final List<Location> locations;

    public Location(@JsonProperty("displayName") String name,
                    @JsonProperty("uid") String ID,
                    @JsonProperty("$type") String type,
                    @JsonProperty("functions") List<String> functionsID,
                    @JsonProperty("locations") List<Location> locations,
                    @JsonProperty("locationType") String locationType) {
        this.name = name;
        this.ID = ID;
        this.type = type;
        this.locationType = locationType;
        this.functionsID = functionsID;
        this.locations = locations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    private List<String> getFunctionsID() {
        return functionsID;
    }


    public List<Function> getFunctions(UIConfig uiConfig) {
        List<Function> result = new ArrayList<>();
        List<Function> functions = uiConfig.getFunctions();
        for (Function function : functions) {
            if (functionsID.contains(function.getID())) {
                result.add(function);
            }
        }
        return result;
    }


    @Override
    public String toString() {
        return "Location{" +
                "\nname='" + name + '\'' +
                "\n, ID='" + ID + '\'' +
                "\n, type='" + type + '\'' +
                "\n, locationType='" + locationType + '\'' +
                "\n, functionsID=" + functionsID +
                "\n, locations=" + locations +
                "\n}";
    }
}