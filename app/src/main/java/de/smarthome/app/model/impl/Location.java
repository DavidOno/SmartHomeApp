package de.smarthome.app.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class Location {

    public static final Location ROOT = new Location();

    private String name;
    private String ID;
    private String type;
    private String locationType;
    private List<String> functionsID;
    private List<Location> locations;
    private Location parentLocation;

    private Location(){

    }

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

    public List<String> getFunctionsID() {
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

    public Location getParentLocation(){
        return parentLocation;
    }

    public void initParentLocation(Location parent){
        this.parentLocation = parent;
        for(Location child : getLocations()){
            child.initParentLocation(this);
        }
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
