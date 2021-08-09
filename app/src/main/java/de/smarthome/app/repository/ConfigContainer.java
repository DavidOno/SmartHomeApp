package de.smarthome.app.repository;

import android.app.Application;
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
    private final ToastUtility toastUtility = ToastUtility.getInstance();

    private UIConfig uiConfig;
    private ChannelConfig channelConfig;
    private BeaconLocations beaconLocations;
    private BoundariesConfig boundariesConfig;

    private Location selectedLocation = null;
    private Function selectedFunction = null;

    private MutableLiveData<List<Location>> locationList = new MutableLiveData<>();
    private MutableLiveData<Map<Function, Function>> functionMap = new MutableLiveData<>();
    private MutableLiveData<Map<Datapoint, Datapoint>> dataPointMap = new MutableLiveData<>();
    private MutableLiveData<Map<Datapoint, BoundaryDataPoint>> boundaryMap = new MutableLiveData<>();

    private MutableLiveData<Map<String, String>> statusUpdateMap = new MutableLiveData<>();
    private MutableLiveData<Map<String, String>> statusGetValueMap = new MutableLiveData<>();

    //TODO: Remove together with StorageWriter after testing
    private Application parentApplication = null;
    public void initSelectedFunction(Function function) {
        setSelectedFunction(function);
        if(function != null) {
            initBoundaryMap();
            initDataPointMap(function);
        }
    }
    //TODO: Remove together with StorageWriter after testing
    public void setParentApplication(Application parentApplication) {
        this.parentApplication = parentApplication;
    }

    public void setSelectedFunction(Function function) {
        selectedFunction = function;
    }

    public Function getSelectedFunction() {
        return selectedFunction;
    }

    public void initSelectedLocation(Location newLocation) {
        setSelectedLocation(newLocation);
        if(newLocation != null) {
            initFunctionMap(selectedLocation);
        }
    }

    public void setSelectedLocation(Location newLocation) {
        selectedLocation = newLocation;
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public void initNewUIConfig(UIConfig newUiConfig) {
        setUIConfig(newUiConfig);
        uiConfig.initParentLocations();

        initLocationList();
        Repository.getInstance().initBeaconObserver();

        if (selectedLocation != null) {
            checkForCurrentlySelectedLocation();
        }
        if (selectedFunction != null) {
            checkForCurrentlySelectedFunction();
        }
    }

    public void setUIConfig(UIConfig newUiConfig) {
        uiConfig = newUiConfig;
    }

    public UIConfig getUIConfig() {
        return uiConfig;
    }

    public void setBeaconLocations(BeaconLocations newBeaconConfig) {
        beaconLocations = newBeaconConfig;
    }

    public BeaconLocations getBeaconLocations() {
        return beaconLocations;
    }

    public void setChannelConfig(ChannelConfig newChannelConfig) {
        channelConfig = newChannelConfig;
    }

    public ChannelConfig getChannelConfig() {
        return channelConfig;
    }

    public void setBoundaryConfig(BoundariesConfig newBoundaryConfig) {
        boundariesConfig = newBoundaryConfig;
    }

    private void checkForCurrentlySelectedLocation() {
        boolean notFound = true;
        if(uiConfig != null && selectedLocation != null){
            for (Location loc : uiConfig.getAllLocations()) {
                if (loc.getName().equals(selectedLocation.getName())) {
                    initSelectedLocation(loc);
                    notFound = false;
                    break;
                }
            }
            if(notFound){
                toastUtility.prepareToast("Current Location was not fund in new UIConfig!");
            }
        }
    }

    private void checkForCurrentlySelectedFunction() {
        boolean notFound;
        if(selectedLocation != null && selectedFunction != null && uiConfig != null) {
            notFound = isNotFoundInSelectedLocation( selectedLocation);
            if(notFound)
                notFound = isNotFoundInParentLocations();
            if (notFound) {
                toastUtility.prepareToast("Selected Function was not fund in new UIConfig!");
            }
        }
    }

    private boolean isNotFoundInSelectedLocation(Location selectedLocation) {
        for (Function func : selectedLocation.getFunctions(uiConfig)) {
            if (func.getName().equals(selectedFunction.getName())) {
                initSelectedFunction(func);
                return false;
            }
        }
        return true;
    }

    private boolean isNotFoundInParentLocations() {
        boolean result = true;
        if (selectedLocation.getParentLocation() != null){
            Location parent = selectedLocation.getParentLocation();
            while(!parent.equals(Location.ROOT)){
                result = isNotFoundInSelectedLocation(parent);
            }
        }
        return result;
    }

    public MutableLiveData<List<Location>> getLocationList() {
        return locationList;
    }

    private void initLocationList() {
        if(uiConfig != null){
            List<Location> allLocations = new ArrayList<>(uiConfig.getLocations());
            for (Location location : uiConfig.getLocations()) {
                location.getAllChildrenFromLocation(allLocations);
            }
            setLocationList(allLocations);
        }
    }

    public void setLocationList(List<Location> allLocations) {
        locationList.postValue(allLocations);
    }

    public void setFunctionMap(Map<Function, Function> completeFunctionMap) {
        functionMap.postValue(completeFunctionMap);
    }

    public MutableLiveData<Map<Function, Function>> getFunctionMap() {
        return functionMap;
    }

    private void initFunctionMap(Location viewedLocation) {
        Map<Function, Function> completeFunctionMap = mapStatusFunctionToFunction(viewedLocation);

        if (viewedLocation.getParentLocation() != null){
            Location parent = viewedLocation.getParentLocation();
            while(!parent.equals(Location.ROOT)) {
                completeFunctionMap.putAll(mapStatusFunctionToFunction(parent));
                parent = parent.getParentLocation();
            }
        }
        //InternalStorageWriter.writeFileOnInternalStorage(parentApplication.getApplicationContext(), "GIRA", "2. initFunctionMap\n");
        setFunctionMap(completeFunctionMap);
    }

    private LinkedHashMap<Function, Function> mapStatusFunctionToFunction(Location location) {
        LinkedHashMap<Function, Function> completeFunctionMap = new LinkedHashMap<>();
        if(uiConfig != null) {
            for (Function func : location.getFunctions(uiConfig)) {
                Function functionStatus = null;
                if (!func.isStatusFunction()) {
                    for (Function comparedFunction : location.getFunctions(uiConfig)) {
                        if (isMatchingStatusFunction(func, comparedFunction)) {
                            functionStatus = comparedFunction;
                            break;
                        }
                    }
                    completeFunctionMap.put(func, functionStatus);
                }
            }
        }
        return completeFunctionMap;
    }

    private boolean isMatchingStatusFunction(Function func, Function comparedFunction) {
        String regex = "_";
        return comparedFunction.isStatusFunction() &&
                func.getName().split(regex)[0].equals(
                        comparedFunction.getName().split(regex)[0]);
    }

    private void initBoundaryMap() {
        Map<Datapoint, BoundaryDataPoint> completeFunctionMap = mapBoundaryToFunction();
        setBoundaryMap(completeFunctionMap);
    }

    public void setBoundaryMap(Map<Datapoint, BoundaryDataPoint> completeFunctionMap) {
        boundaryMap.postValue(completeFunctionMap);
    }

    public LiveData<Map<Datapoint, BoundaryDataPoint>> getBoundaryMap() {
        return boundaryMap;
    }

    private LinkedHashMap<Datapoint, BoundaryDataPoint> mapBoundaryToFunction() {
        LinkedHashMap<Datapoint, BoundaryDataPoint> datapointBoundaryDataPointMap = new LinkedHashMap<>();
        if(boundariesConfig != null && selectedLocation != null && selectedFunction != null) {
            String regex = "_";
            for (Boundary boundary : boundariesConfig.getBoundaries()) {
                if (boundary.getLocation().equals(selectedLocation.getName()) &&
                        boundary.getName().split(regex)[0].equals(selectedFunction.getName().split(regex)[0])) {

                    datapointBoundaryDataPointMap.putAll(mapCorrespondingBoundaryDataPoint(boundary));
                    break;
                }
            }
        }
        return datapointBoundaryDataPointMap;
    }

    private LinkedHashMap<Datapoint, BoundaryDataPoint> mapCorrespondingBoundaryDataPoint(Boundary boundary) {
        LinkedHashMap<Datapoint, BoundaryDataPoint> datapointBoundaryDataPointMap = new LinkedHashMap<>();
        if(selectedFunction != null) {
            for (BoundaryDataPoint bdp : boundary.getDatapoints()) {
                for (Datapoint dp : selectedFunction.getDataPoints())
                    if (bdp.getName().equals(dp.getName())) {
                        datapointBoundaryDataPointMap.put(dp, bdp);
                        break;
                    }
            }
        }
        return datapointBoundaryDataPointMap;
    }

    private void initDataPointMap(Function function) {
        //InternalStorageWriter.writeFileOnInternalStorage(parentApplication.getApplicationContext(), "GIRA", "2. initDatapointMap\n");
        Map<Datapoint, Datapoint> newValue = new LinkedHashMap<>();
        if (functionMap != null && functionMap.getValue() != null && functionMap.getValue().get(function) != null) {
                for (int i = 0; i < function.getDataPoints().size(); i++) {
                    if (functionMap.getValue().get(function).getDataPoints().size() > i) {
                        newValue.put(function.getDataPoints().get(i), functionMap.getValue().get(function).getDataPoints().get(i));
                    } else {
                        newValue.put(function.getDataPoints().get(i), null);
                    }
                }
            } else {
                for (Datapoint datapoint : function.getDataPoints()) {
                    newValue.put(datapoint, null); }
        }
        setDataPointMap(newValue);
    }

    public void setDataPointMap(Map<Datapoint, Datapoint> newValue) {
        dataPointMap.postValue(newValue);
    }

    public MutableLiveData<Map<Datapoint, Datapoint>> getDataPointMap() {
        return dataPointMap;
    }

    public void initStatusUpdateMap(String functionUID, String value) {
        Map<String, String> newValue = new HashMap<>();
        newValue.put(functionUID, value);
        setStatusUpdateMap(newValue);
    }

    public void setStatusUpdateMap(Map<String, String> newValue){
        statusUpdateMap.postValue(newValue);
    }

    public MutableLiveData<Map<String, String>> getStatusUpdateMap() {
        return statusUpdateMap;
    }

    public void setStatusGetValueMap(Map<String, String> newValues) {
        statusGetValueMap.postValue(newValues);
    }

    public MutableLiveData<Map<String, String>> getStatusGetValueMap() {
        return statusGetValueMap;
    }






    public void fillWithDummyValueAllConfigs() {
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
        functionIDs.add("aae9");
        functionIDs.add("aafe");
        List<Location> loc = new ArrayList<>();

        return new Location("Esszimmer", "aaej", "4", functionIDs, loc, "Room");
    }

    private Location createWohnen() {
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aaet");
        functionIDs.add("aae2");
        functionIDs.add("aae8");
        functionIDs.add("aae9");

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
        functionIDs.add("aae9");

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

            ArrayList<Datapoint> dataPoints9 = new ArrayList<>();
            dataPoints9.add(new Datapoint("aajf11", "Current"));
            dataPoints9.add(new Datapoint("aajfd11", "Set-Point"));
            dataPoints9.add(new Datapoint("aajfa11", "OnOff"));
            funcList1.add(new Function("Temperatur_Status", "aae9", "de.gira.schema.channels.RoomTemperatureSwitchable", "de.gira.schema.functions.KNX.HeatingCooling", dataPoints9));

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
            initNewUIConfig(newUiConfig);
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
            channelDatapoints2.add(new ChannelDatapoint("Current", "Float", "rwe"));
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
            channelConfig = output;
        }).start();
    }

    private void fillWithDummyValueBeaconConfig() {
        String locationConfigString = "{\n" +
                "\t\"locations\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aafb\",\n" +
                "\t\t\t\"beaconId\": \"7b44b47b-52a1-5381-90c2-f09b6838c5d420\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aaej\",\n" +
                "\t\t\t\"beaconId\": \"7b44b47b-52a1-5381-90c2-f09b6838c5d470\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aaex\",\n" +
                "\t\t\t\"beaconId\": \"7b44b47b-52a1-5381-90c2-f09b6838c5d490\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        try {
            setBeaconLocations(mapper.readValue(locationConfigString, new TypeReference<BeaconLocations>() {}));
            Repository.getInstance().initBeaconObserver();
        } catch (Exception e) {
            Log.d(TAG, "BeaconConfig Exception " + e.toString());
            e.printStackTrace();
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

        boundariesConfig = new BoundariesConfig(z);
    }
}