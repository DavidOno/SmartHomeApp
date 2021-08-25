package de.smarthome.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.utility.ToastUtility;
import de.smarthome.beacons.BeaconLocations;

/**
 * This class is the data storage of the repository.
 * It contains f.e. all the configs send the gira server and the callbackserver.
 * The data that is used by the ui is stored in livedata objects so any change can be propagated to ui
 */
public class ConfigContainer {
    private static final String TAG = "ConfigContainer";
    private final ToastUtility toastUtility = ToastUtility.getInstance();

    private UIConfig uiConfig;
    private ChannelConfig channelConfig;
    private BeaconLocations beaconLocations;
    private BoundariesConfig boundariesConfig;

    private Location selectedLocation = null;
    private Function selectedFunction = null;

    private final MutableLiveData<List<Location>> locationList = new MutableLiveData<>();
    private final MutableLiveData<Map<Function, Function>> functionMap = new MutableLiveData<>();
    private final MutableLiveData<Map<Datapoint, Datapoint>> dataPointMap = new MutableLiveData<>();
    private final MutableLiveData<Map<Datapoint, BoundaryDataPoint>> boundaryMap = new MutableLiveData<>();

    private final MutableLiveData<Map<String, String>> statusUpdateMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> statusGetValueMap = new MutableLiveData<>();

    /**
     * Sets the given function as selectedFunction and
     * fills the boundaryMap with the datapoints of the function and their corresponding boundarydatapoints and
     * fills the datapointMap with the datapoints of the function and their corresponding datapoints of the status function
     * @param function Function that should initialised as selectedFunction
     */
    public void initSelectedFunction(Function function) {
        setSelectedFunction(function);
        if (function != null) {
            initBoundaryMap();
            initDataPointMap(function);
        }
    }

    public void setSelectedFunction(Function function) {
        selectedFunction = function;
    }

    public Function getSelectedFunction() {
        return selectedFunction;
    }

    /**
     * Sets the given location as selectedLocation and
     * fills the functionMap with the functions of the location and their corresponding status functions
     * @param location Location that should initialised as selectedLocation
     */
    public void initSelectedLocation(Location location) {
        setSelectedLocation(location);
        if(location != null) {
            initFunctionMap(selectedLocation);
        }
    }

    public void setSelectedLocation(Location newLocation) {
        selectedLocation = newLocation;
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    /**
     * Sets the given uiconfig and initialises for the contained the parent location.
     * Initialises the locationList with every location contained in the config.
     * Starts initialisation of beaconObserver in Repository.
     * Checks if selectedLocation/selectedFunction are contained in the new uiconfig.
     * @param uiConfig UIConfig that should be initialised
     */
    public void initNewUIConfig(UIConfig uiConfig) {
        uiConfig.initParentLocations();
        if(!uiConfig.equals(this.uiConfig)){
            setUIConfig(uiConfig);

            initLocationList();
            Repository.getInstance().initBeaconObserver();

            if (selectedLocation != null) {
                checkForCurrentlySelectedLocation();
            }
            if (selectedFunction != null) {
                checkForCurrentlySelectedFunction();
            }
        }
    }

    public void setUIConfig(UIConfig newUiConfig) {
        uiConfig = newUiConfig;
    }

    public UIConfig getUIConfig() {
        return uiConfig;
    }

    public void setBeaconLocations(BeaconLocations newBeaconLocations) {
        beaconLocations = newBeaconLocations;
    }

    public BeaconLocations getBeaconLocations() {
        return beaconLocations;
    }

    public void initBeaconLocations(BeaconLocations newBeaconLocations){
        if(!newBeaconLocations.equals(this.beaconLocations)){
            setBeaconLocations(newBeaconLocations);
            Repository.getInstance().initBeaconObserver();
        }
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
                toastUtility.prepareToast("Current Location was not found in new UIConfig!");
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
                toastUtility.prepareToast("Selected Function was not found in new UIConfig!");
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

    public void setBoundaryMap(Map<Datapoint, BoundaryDataPoint> boundaryMap) {
        this.boundaryMap.postValue(boundaryMap);
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
        Map<Datapoint, Datapoint> newValue = new LinkedHashMap<>();
        if (functionMap.getValue() != null && functionMap.getValue().get(function) != null) {
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

    /**
     * Puts inputs into the statusUpdateMap
     * @param functionUID ID of the datapoint that was updated
     * @param value Value of the datapoint that was updated
     */
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
}