package de.smarthome.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UIConfig {

    private List<Function> functions;
    private List<Location> locations;
    private String uid;

    public UIConfig(@JsonProperty("functions") List<Function> functions, @JsonProperty("locations")List<Location> locations, @JsonProperty("uid")String uid) {
        this.functions = functions;
        this.locations = locations;
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void initParentLocations(){
        for(Location root : getLocations()){
            root.initParentLocation(Location.ROOT);
        }
    }

    public void updateValue(String id, Object value){
        for(Function function: getFunctions()){
            if(function.isStatusFunction()) {
                Optional<Datapoint> correspondingDataPoint = function.getCorrespondingDataPoint(id);
                if (correspondingDataPoint.isPresent()) {
                    correspondingDataPoint.get().setValue(value);
                    return;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "UIConfig{\n" +
                "functions=" + functions +
                "\n, locations=" + locations +
                "\n, uid='" + uid + '\'' +
                "\n}";
    }

    public Optional<Location> getLocation(Optional<String> roomUID) {
        if(roomUID.isPresent()) {
            for(Location root : getLocations()) {
                List<Location> result = new ArrayList<>();
                getLocation(root, result, roomUID.get());

                return Optional.of(result.get(0));
            }
        }
        return Optional.empty();
    }

    private void getLocation(Location locationParent, List<Location> result, String roomId) {
        if(locationParent.getID().equals(roomId)) {
            result.add(locationParent);
            return;
        }

        for(Location location : locationParent.getLocations()) {
            getLocation(location, result, roomId);
        }
    }

    public List<Location> getAllLocations(){
        List<Location> resultList = locations;
        for(Location loc : locations){
            loc.getAllChildrenFromLocation(resultList);
        }
        return resultList;
    }
}
