package de.smarthome.app.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.beacons.BeaconObserverImplementation;
import de.smarthome.beacons.BeaconObserverSubscriber;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.responses.CallBackServiceInput;
import de.smarthome.app.model.responses.CallbackValueInput;
import de.smarthome.app.model.responses.ServiceEvent;
import de.smarthome.beacons.DefaultBeaconManagerCreator;
import de.smarthome.server.CallbackSubscriber;
import de.smarthome.server.MyFirebaseMessagingService;

public class Repository implements CallbackSubscriber, BeaconObserverSubscriber {
    private static final String TAG = "Repository";
    private static Repository INSTANCE;

    private ConfigContainer configContainer;
    private ServerCommunicator serverCommunicator;

    private BeaconObserverImplementation beaconObserver;
    private Application parentApplication;
    private Location beaconLocation = null;
    private MutableLiveData<Boolean> beaconCheck = new MutableLiveData<>();

    public static Repository getInstance(@Nullable Application application) {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
            INSTANCE.configContainer = ConfigContainer.getInstance();
            if(application != null){
                INSTANCE.serverCommunicator = ServerCommunicator.getInstance(application);
                INSTANCE.parentApplication = application;
            }
        }
        return INSTANCE;
    }
    //TODO: Remove after Testing
    public void fillWithDummyValues(){
        configContainer.fillWithDummyValueAllConfigs();
    }

    public void setLoginStatus(boolean update){
        serverCommunicator.setLoginStatus(update);
    }

    public LiveData<Boolean> getLoginStatus() {
        return serverCommunicator.getLoginStatusForUser();
    }

    public void setSelectedFunction(Function function) {
        configContainer.initSelectedFunction(function);
    }

    public Function getSelectedFunction() {
        return configContainer.getSelectedFunction();
    }

    public void setSelectedLocation(Location newLocation) {
        configContainer.initSelectedLocation(newLocation);
    }

    public Location getSelectedLocation() {
        return configContainer.getSelectedLocation();
    }

    public void setBeaconCheckFalse() {
        beaconCheck.postValue(false);
    }

    public LiveData<Boolean> getBeaconCheck() {
        return beaconCheck;
    }

    public Location getBeaconLocation() {
        return beaconLocation;
    }

    public ChannelConfig getChannelConfig() {
        return configContainer.getChannelConfig();
    }

    public void confirmBeaconLocation() {
        setSelectedLocation(beaconLocation);
        beaconLocation = null;
    }

    public void initBeaconObserver() {
        beaconObserver = new BeaconObserverImplementation(parentApplication, parentApplication.getApplicationContext(),
                configContainer.getUIConfig(), configContainer.getBeaconLocations(),
                new DefaultBeaconManagerCreator());
        beaconObserver.subscribe(this);
        beaconObserver.init();
        setBeaconCheckFalse();
    }

    public void requestRegisterUser(Credential credential) {
        serverCommunicator.initialisationOfApplication(credential);
        subscribeToMyFirebaseMessagingService();
    }

    private void subscribeToMyFirebaseMessagingService() {
        MyFirebaseMessagingService.getValueObserver().subscribe(this);
        MyFirebaseMessagingService.getServiceObserver().subscribe(this);
    }

    public void requestSetValue(String id, String value) {
        serverCommunicator.requestSetValue(id, value);
    }

    public void requestGetValue(List<String> ids) {
        serverCommunicator.requestGetValue(ids);
    }

    public MutableLiveData<List<Location>> getLocationList() {
        return configContainer.getLocationList();
    }

    public MutableLiveData<Map<Function, Function>> getFunctionMap() {
        requestCurrentFunctionValues(Objects.requireNonNull(configContainer.getFunctionMap().getValue()));
        return configContainer.getFunctionMap();
    }

    //TODO: Check if this works fine
    private void requestCurrentFunctionValues(Map<Function, Function> functionMap){
        List<Function> functionList = new ArrayList<>(functionMap.keySet());
        List<String> requestList = new ArrayList<>();
        for(Function func : functionList){
            if(configContainer.getChannelConfig().isFirstDataPointBinary(func)){
                if(functionMap.get(func) != null){
                    requestList.add(functionMap.get(func).getDataPoints().get(0).getID());
                }
            }else{
                if(configContainer.getChannelConfig().isStatusViewHolder(func)){
                    requestList.add(functionMap.get(func).getDataPoints().get(0).getID());
                }
            }
        }
        if(!requestList.isEmpty()){
            requestGetValue(requestList);
        }
    }

    public LiveData<Map<Datapoint, BoundaryDataPoint>> getBoundaryMap() {
        return configContainer.getBoundaryMap();
    }

    public MutableLiveData<Map<Datapoint, Datapoint>> getDataPointMap() {
        requestCurrentDataPointValues(Objects.requireNonNull(configContainer.getDataPointMap().getValue()));
        return configContainer.getDataPointMap();
    }

    //TODO: Check if this works fine
    private void requestCurrentDataPointValues(Map<Datapoint, Datapoint> dataPointMap){
        List<Datapoint> dataPointList = new ArrayList<>(dataPointMap.keySet());
        List<String> requestList = new ArrayList<>();
        for(Datapoint dp : dataPointList){
            if(dataPointMap.get(dp) != null && !dataPointMap.get(dp).getID().equals(dp.getID())){
                requestList.add(dataPointMap.get(dp).getID());
            }
        }
        requestGetValue(requestList);
    }

    public MutableLiveData<Map<String, String>> getStatusUpdateMap() {
        return configContainer.getStatusUpdateMap();
    }

    public MutableLiveData<Map<String, String>> getStatusGetValueMap() {
        return configContainer.getStatusGetValueMap();
    }

    public void sendBeaconLocationToConfigContainer(BeaconLocations newBeaconConfig){
        configContainer.setBeaconLocations(newBeaconConfig);
        initBeaconObserver();
    }

    public void setBoundaryConfig(BoundariesConfig newBoundariesConfig){
        configContainer.setBoundaryConfig(newBoundariesConfig);
    }

    public void setChannelConfig(ChannelConfig newChannelConfig){
        configContainer.setChannelConfig(newChannelConfig);
    }

    public void initNewUIConfig(UIConfig newUIConfig){
        configContainer.initNewUIConfig(newUIConfig);
    }

    @Override
    public void update(CallbackValueInput input) {
        if (input.getEvent() == null && input.getValue() != null) {
            String value = String.valueOf(input.getValue());
            String uID = input.getUid();
            configContainer.initStatusUpdateMap(uID, value);
        }
    }

    @Override
    public void update(CallBackServiceInput input) {
        if (input.getServiceEvents() != null) {
            for (ServiceEvent serviceEvent : input.getServiceEvents()) {
                switch (serviceEvent.getEvent()) {
                    case STARTUP:
                        Log.d(TAG, "Server has started.");
                        serverCommunicator.getSavedCredentialsForLoginAfterRestart();
                        break;
                    case RESTART:
                        Log.d(TAG, "Server got restarted.");
                        break;
                    case UI_CONFIG_CHANGED:
                        Log.d(TAG, "UI_CONFIG_CHANGED");
                        serverCommunicator.requestUIConfigAfterRestart();
                        break;
                    case PROJECT_CONFIG_CHANGED:
                        Log.d(TAG, "PROJECT_CONFIG_CHANGED");
                        serverCommunicator.requestAdditionalConfigsAfterRestart();
                        break;
                    default:
                        Log.d(TAG, "Unknown CallBackServiceInputEvent");
                        break;
                }
            }
        }
    }

    @Override
    public void update(Location newLocation) {
        if(newLocation != configContainer.getSelectedLocation()){
            beaconLocation = newLocation;
            beaconCheck.postValue(true);
        }
    }

    public void unsubscribeFromEverything() {
        try {
            MyFirebaseMessagingService.getValueObserver().unsubscribe(this);
            MyFirebaseMessagingService.getServiceObserver().unsubscribe(this);
            serverCommunicator.unsubscribeFromEverything();
            beaconObserver.unsubscribe();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
}