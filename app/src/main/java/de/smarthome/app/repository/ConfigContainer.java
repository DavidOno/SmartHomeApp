package de.smarthome.app.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.Boundary;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.model.configs.Channel;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.configs.ChannelDatapoint;
import de.smarthome.app.utility.ToastUtility;
import de.smarthome.beacons.BeaconLocations;

public class ConfigContainer {
    private static final String TAG = "ConfigContainer";
    private static ConfigContainer instance;
    private final ToastUtility toastUtility = ToastUtility.getInstance();

    private UIConfig smartHomeUiConfig;
    private ChannelConfig smartHomeChannelConfig;
    private BeaconLocations smartHomeBeaconLocations;
    private BoundariesConfig smartHomeBoundariesConfig;

    private Location selectedLocation = null;
    private Function selectedFunction = null;

    private MutableLiveData<List<Location>> locationList = new MutableLiveData<>();
    private MutableLiveData<Map<Function, Function>> functionMap = new MutableLiveData<>();
    private MutableLiveData<Map<Datapoint, Datapoint>> dataPointMap = new MutableLiveData<>();
    private MutableLiveData<Map<Datapoint, BoundaryDataPoint>> boundaryMap = new MutableLiveData<>();

    private MutableLiveData<Map<String, String>> statusUpdateMap = new MutableLiveData<>();
    private MutableLiveData<Map<String, String>> statusGetValueMap = new MutableLiveData<>();

    public static ConfigContainer getInstance() {
        if (instance == null) {
            instance = new ConfigContainer();
            //TODO: Remove after testing!!
            instance.fillWithDummyValueAllConfigs();
        }
        return instance;
    }

    public void setSelectedFunction(Function function) {
        selectedFunction = function;
        if(function != null) {
            updateBoundaryMap();
            updateDataPointMap(function);
        }
    }

    public Function getSelectedFunction() {
        return selectedFunction;
    }

    public void setSelectedLocation(Location newLocation) {
        selectedLocation = newLocation;
        if(newLocation != null) {
            updateFunctionMap(selectedLocation);
        }
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public void setUIConfig(UIConfig newUiConfig) {
        smartHomeUiConfig = newUiConfig;
        smartHomeUiConfig.initParentLocations();

        updateLocationList();
        if (selectedLocation != null) {
            checkCurrentSelectedLocation();
            Repository.getInstance(null).initBeaconObserver();
        }
        if (selectedFunction != null) {
            checkCurrentSelectedFunction();
        }
    }

    public UIConfig getSmartHomeUiConfig() {
        return smartHomeUiConfig;
    }

    public void setBeaconConfig(BeaconLocations newBeaconConfig) {
        smartHomeBeaconLocations = newBeaconConfig;
        Repository.getInstance(null).initBeaconObserver();
    }

    public BeaconLocations getSmartHomeBeaconLocations() {
        return smartHomeBeaconLocations;
    }

    public void setChannelConfig(ChannelConfig newChannelConfig) {
        smartHomeChannelConfig = newChannelConfig;
    }

    public ChannelConfig getSmartHomeChannelConfig() {
        return smartHomeChannelConfig;
    }

    public void setBoundaryConfig(BoundariesConfig newBoundaryConfig) {
        smartHomeBoundariesConfig = newBoundaryConfig;
    }

    private void checkCurrentSelectedLocation() {
        boolean notFound = true;
        for (Location loc : smartHomeUiConfig.getAllLocations()) {
            if (loc.getName().equals(selectedLocation.getName())) {
                setSelectedLocation(loc);
                notFound = false;
                break;
            }
        }
        if(notFound){
            toastUtility.prepareToast("Current Location was not fund in new UIConfig!");
        }
    }

    private void checkCurrentSelectedFunction() {
        boolean notFound = true;
        for (Function func : selectedLocation.getFunctions(smartHomeUiConfig)) {
            if (func.getName().equals(selectedFunction.getName())) {
                setSelectedFunction(func);
                notFound = false;
                break;
            }
        }
        if(notFound){
            toastUtility.prepareToast("Selected Function was not fund in new UIConfig!");
        }
    }

    public MutableLiveData<List<Location>> getLocationList() {
        return locationList;
    }

    public void updateLocationList() {
        List<Location> allLocations = new ArrayList<>(smartHomeUiConfig.getLocations());
        for (Location location : allLocations) {
            location.getAllChildrenFromLocation(allLocations);
        }
        locationList.postValue(allLocations);
    }

    public MutableLiveData<Map<Function, Function>> getFunctionMap() {
        return functionMap;
    }

    public void updateFunctionMap(Location viewedLocation) {
        Map<Function, Function> completeFunctionMap = mapStatusFunctionToFunction(viewedLocation);

        if (!viewedLocation.getParentLocation().equals(Location.ROOT)) {
            completeFunctionMap.putAll(mapStatusFunctionToFunction(viewedLocation.getParentLocation()));
        }
        functionMap.postValue(completeFunctionMap);
    }

    private LinkedHashMap<Function, Function> mapStatusFunctionToFunction(Location location) {
        LinkedHashMap<Function, Function> completeFunctionMap = new LinkedHashMap<>();
        String regex = "_";
        for (Function func : location.getFunctions(smartHomeUiConfig)) {
            Function functionStatus = null;
            if (!func.isStatusFunction()) {
                for (Function comparedFunction : location.getFunctions(smartHomeUiConfig)) {
                    if (comparedFunction.isStatusFunction() &&
                            //If they share the same name "Light_on" and "Light_Status"
                            func.getName().split(regex)[0].equals(
                                    comparedFunction.getName().split(regex)[0])) {
                            functionStatus = comparedFunction;
                            break;
                    }
                }
                completeFunctionMap.put(func, functionStatus);
            }
        }
        return completeFunctionMap;
    }

    public void updateBoundaryMap() {
        Map<Datapoint, BoundaryDataPoint> completeFunctionMap = mapBoundaryToFunction();
        boundaryMap.postValue(completeFunctionMap);
    }

    public LiveData<Map<Datapoint, BoundaryDataPoint>> getBoundaryMap() {
        return boundaryMap;
    }

    private LinkedHashMap<Datapoint, BoundaryDataPoint> mapBoundaryToFunction() {
        LinkedHashMap<Datapoint, BoundaryDataPoint> datapointBoundaryDataPointMap = new LinkedHashMap<>();
        String regex = "_";
        for (Boundary boundary : smartHomeBoundariesConfig.getBoundaries()) {
            if (boundary.getLoaction().equals(selectedLocation.getName()) &&
                    boundary.getName().split(regex)[0].equals(selectedFunction.getName().split(regex)[0])) {

                datapointBoundaryDataPointMap.putAll(findCorrespondingBoundaryDataPoint(boundary));
                break;
            }
        }
        return datapointBoundaryDataPointMap;
    }

    private LinkedHashMap<Datapoint, BoundaryDataPoint> findCorrespondingBoundaryDataPoint(Boundary boundary) {
        LinkedHashMap<Datapoint, BoundaryDataPoint> datapointBoundaryDataPointMap = new LinkedHashMap<>();
        for (BoundaryDataPoint bdp : boundary.getDatapoints()) {
            for (Datapoint dp : selectedFunction.getDataPoints())
                if (bdp.getName().equals(dp.getName())) {
                    datapointBoundaryDataPointMap.put(dp, bdp);
                    break;
                }
        }
        return datapointBoundaryDataPointMap;
    }

    public void updateDataPointMap(Function function) {
        Map<Datapoint, Datapoint> newValue = new LinkedHashMap<>();
        if (functionMap.getValue().get(function) != null) {
            for (int i = 0; i < function.getDataPoints().size(); i++) {
                newValue.put(function.getDataPoints().get(i), functionMap.getValue().get(function).getDataPoints().get(i));
            }
        } else {
            for (Datapoint datapoint : function.getDataPoints()) {
                newValue.put(datapoint, null);
            }
        }
        dataPointMap.postValue(newValue);
    }

    public MutableLiveData<Map<Datapoint, Datapoint>> getDataPointMap() {
        return dataPointMap;
    }

    public void updateStatusUpdateMap(String functionUID, String value) {
        Map<String, String> newValue = new HashMap<>();
        newValue.put(functionUID, value);
        statusUpdateMap.postValue(newValue);
    }

    public MutableLiveData<Map<String, String>> getStatusUpdateMap() {
        return statusUpdateMap;
    }

    public void updateStatusGetValueMap(Map<String, String> newValues) {
        statusGetValueMap.postValue(newValues);
    }

    public MutableLiveData<Map<String, String>> getStatusGetValueMap() {
        return statusGetValueMap;
    }






    private void fillWithDummyValueAllConfigs() {
        fillWithDummyValuesUIConfig();
        fillWithDummyValuesChannelConfig();
        fillWithDummyValueBeaconConfig();
        fillWithDummyValueBoundaryConfig();
    }

    private Location createEssen() {
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aael");
        functionIDs.add("aae4");

        functionIDs.add("aae8");
        functionIDs.add("aafe");
        List<Location> loc = new ArrayList<>();

        return new Location("Esszimmer", "aaej", "4", functionIDs, loc, "Room");
    }

    private Location createWohnen() {
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aaet");
        functionIDs.add("aae2");
        functionIDs.add("aae8");

        functionIDs.add("aaafe");

        List<Location> loc = new ArrayList<>();

        return new Location("Wohnzimmer", "aaex", "4", functionIDs, loc, "Room");
    }

    private Location createTerrasse() {
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aafe");
        functionIDs.add("aafg");

        functionIDs.add("aael");
        functionIDs.add("aae8");

        List<Location> loc = new ArrayList<>();

        return new Location("Terrasse", "aafb", "4", functionIDs, loc, "Room");
    }

    private void fillWithDummyValuesUIConfig() {
        new Thread(() -> {
            ArrayList<Function> funcList1 = new ArrayList<>();
            List<String> functionIDs = new ArrayList<>();
            functionIDs.add("aaab");

            ArrayList<Datapoint> dataPoints = new ArrayList<>();
            dataPoints.add(new Datapoint("aauy", "OnOff"));

            funcList1.add(new Function("Tischleuchte_schalten", "aael", "de.gira.schema.channels.Switch", "de.gira.schema.functions.Switch", dataPoints));

            ArrayList<Datapoint> dataPoints2 = new ArrayList<>();
            dataPoints2.add(new Datapoint("aajc", "OnOff"));
            funcList1.add(new Function("Tischleuchte_Status", "aae4", "de.gira.schema.channels.Switch", "de.gira.schema.functions.Switch", dataPoints2));


            ArrayList<Datapoint> dataPoints3 = new ArrayList<>();
            dataPoints3.add(new Datapoint("aat8", "OnOff"));
            funcList1.add(new Function("Stehleuchte_schalten", "aaet", "de.gira.schema.channels.Switch", "de.gira.schema.functions.Switch", dataPoints3));


            ArrayList<Datapoint> dataPoints4 = new ArrayList<>();
            dataPoints4.add(new Datapoint("aajb", "Set-Point"));
            funcList1.add(new Function("Stehleuchte_Status", "aae2", "de.gira.schema.channels.Switch", "de.gira.schema.functions.Switch", dataPoints4));


            ArrayList<Datapoint> dataPoints5 = new ArrayList<>();
            dataPoints5.add(new Datapoint("aajf", "Current"));
            dataPoints5.add(new Datapoint("aajfd", "Set-Point"));
            dataPoints5.add(new Datapoint("aajfa", "OnOff"));
            funcList1.add(new Function("Temperatur_Wohnen", "aae8", "de.gira.schema.channels.RoomTemperatureSwitchable", "de.gira.schema.functions.KNX.HeatingCooling", dataPoints5));


            ArrayList<Datapoint> dataPoints6 = new ArrayList<>();
            dataPoints6.add(new Datapoint("aajv", "Step-Up-Down"));
            dataPoints6.add(new Datapoint("aaju", "Up-Down"));
            dataPoints6.add(new Datapoint("aajw", "Position"));
            funcList1.add(new Function("Markise_bewegen", "aafe", "de.gira.schema.channels.BlindWithPos", "de.gira.schema.functions.Covering", dataPoints6));

            ArrayList<Datapoint> dataPoints7 = new ArrayList<>();
            dataPoints7.add(new Datapoint("aajv", "Step-Up-Down"));
            dataPoints7.add(new Datapoint("aaju", "Up-Down"));
            dataPoints7.add(new Datapoint("aajx", "Position"));
            funcList1.add(new Function("Markise_Status", "aafg", "de.gira.schema.channels.BlindWithPos", "de.gira.schema.functions.Covering", dataPoints7));

            ArrayList<Datapoint> dataPoints8 = new ArrayList<>();
            dataPoints8.add(new Datapoint("aajv", "Step-Up-Down"));
            dataPoints8.add(new Datapoint("aaju", "Up-Down"));
            dataPoints8.add(new Datapoint("aajx", "Position"));
            funcList1.add(new Function("Alarmanlage", "aaab", "de.gira.schema.channels.BlindWithPos", "de.gira.schema.functions.Covering", dataPoints8));

            List<Location> uiconfigloc = new ArrayList<>();
            List<Location> uiconfigloc2 = new ArrayList<>();

            uiconfigloc.add(createEssen());
            uiconfigloc.add(createWohnen());
            uiconfigloc.add(createTerrasse());

            Location all = new Location("House", "aaaa", "4", functionIDs, uiconfigloc, "Room");
            uiconfigloc2.add(all);

            UIConfig newUiConfig = new UIConfig(funcList1, uiconfigloc2, "cczk");
            //Log.d("Hello", "New UIConfig " + newUiConfig.toString());
            setUIConfig(newUiConfig);
        }).start();
    }

    private void fillWithDummyValuesChannelConfig() {
        new Thread(() -> {
            List<Channel> channelList = new ArrayList<>();
            List<ChannelDatapoint> channelDatapoints = new ArrayList<>();

            channelDatapoints.add(new ChannelDatapoint("OnOff", "Binary", "rwe"));
            Channel c1 = new Channel("de.gira.schema.channels.Switch", channelDatapoints);
            channelList.add(c1);


            List<ChannelDatapoint> channelDatapoints2 = new ArrayList<>();
            channelDatapoints2.add(new ChannelDatapoint("Current", "Float", "re"));
            channelDatapoints2.add(new ChannelDatapoint("Set-Point", "Float", "rwe"));
            channelDatapoints2.add(new ChannelDatapoint("OnOff", "Binary", "rwe"));
            Channel c2 = new Channel("de.gira.schema.channels.RoomTemperatureSwitchable", channelDatapoints2);
            channelList.add(c2);


            List<ChannelDatapoint> channelDatapoints3 = new ArrayList<>();
            channelDatapoints3.add(new ChannelDatapoint("Step-Up-Down", "Binary", "w"));
            channelDatapoints3.add(new ChannelDatapoint("Up-Down", "Binary", "w"));
            channelDatapoints3.add(new ChannelDatapoint("Movement", "Binary", "re"));
            channelDatapoints3.add(new ChannelDatapoint("Position", "Percent", "rwe"));
            channelDatapoints3.add(new ChannelDatapoint("Slat-Position", "Percent", "rwe"));
            Channel c3 = new Channel("de.gira.schema.channels.BlindWithPos", channelDatapoints3);
            channelList.add(c3);

            ChannelConfig output = new ChannelConfig(channelList);
            //Log.d(TAG, output.toString());
            smartHomeChannelConfig = output;
        }).start();
    }

    private void fillWithDummyValueBeaconConfig() {
        String locationConfigString = "{\n" +
                "\t\"locations\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aafb\",\n" +
                "\t\t\t\"beaconId\": \"ebefd083-70a2-47c8-9837-e7b5634df55570\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aaej\",\n" +
                "\t\t\t\"beaconId\": \"2789b1a4-664c-51dd-9d49-df3401de285700\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aaex\",\n" +
                "\t\t\t\"beaconId\": \"7b44b47b-52a1-5381-90c2-f09b6838c5d490\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        try {
            setBeaconConfig(mapper.readValue(locationConfigString, new TypeReference<BeaconLocations>() {
            }));

        } catch (Exception e) {
            Log.d(TAG, "BeaconConfig Exception " + e.toString());
        }
    }

    private void fillWithDummyValueBoundaryConfig() {
        BoundaryDataPoint dataPoint = new BoundaryDataPoint("Position", "20", "90");
        List<BoundaryDataPoint> list = new ArrayList<>();
        list.add(dataPoint);
        Boundary x = new Boundary("Markise_Boundary", "Terrasse", list);

        BoundaryDataPoint dataPoint2 = new BoundaryDataPoint("Position", "10", "30");
        List<BoundaryDataPoint> list2 = new ArrayList<>();
        list2.add(dataPoint2);
        Boundary y = new Boundary("Alarmanlage_Boundary", "House", list2);

        List<Boundary> z = new ArrayList<>();
        z.add(y);
        z.add(x);

        smartHomeBoundariesConfig = new BoundariesConfig(z);
    }
}