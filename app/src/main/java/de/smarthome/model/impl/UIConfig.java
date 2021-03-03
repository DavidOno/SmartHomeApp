package de.smarthome.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UIConfig {

    private List<Function> functions;
    private List<Location> locations;
    private String uid;

    public UIConfig(@JsonProperty("functions") List<Function> functions, @JsonProperty("locations") List<Location> locations, @JsonProperty("uid") String uid) {
        this.functions = functions;
        this.locations = locations;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<Location> getLocations() {
        return locations;
    }


    @Override
    public String toString() {
        return "UIConfig{\n" +
                "functions=" + functions +
                "\n, locations=" + locations +
                "\n, uid='" + uid + '\'' +
                "\n}";
    }


    //(Shop, Badzimmer, Dusche)
    //Shop
    //--Bereich Essen
    //--Bereich Wohnen
    //--Bereich Badzimmer
    //-----Bereich Dusche
    //-----Bereich WC

    /**
     * key: value
     * Bereich Wohnen: (func1, func2, func3)
     * Bereich Shop: (func4)
     *
     * @param roomUID
     * @return
     */
    public Map<Location, List<Function>> getFunctionsForRoomHierarchy(String roomUID) {
        List<Location> locations = searchAllParentLocations(roomUID);
        Map<Location, List<Function>> result = new LinkedHashMap<>();
        for (Location loc : locations) {
            result.put(loc, loc.getFunctions(this));
        }
        return result;
    }

    private List<Location> searchAllParentLocations(String roomUID) {
        List<Location> result = new ArrayList<>();
        List<Location> roots = getLocations();

        for (Location root : roots) {
            result.clear();
            buildHierarchy(root, result, roomUID);
            if (isHierarchyComplete(result)) {
                return result;
            }
        }
        throw new IllegalArgumentException(roomUID + " is not a roomID or not a leaf-node");
    }

    private boolean isHierarchyComplete(List<Location> result) {

        return false;

    }

    private List<Location> buildHierarchy(Location parent, List<Location> result, String roomUID) {
        result.add(parent);
        if (parent.getID().equals(roomUID)) {
            return result;
        }
        for (Location child : parent.getLocations()) {
            buildHierarchy(child, new ArrayList<>(result), roomUID);
        }
        return null;
    }
}