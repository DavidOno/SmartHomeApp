package de.smarthome.app.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import java.util.List;
import java.util.Map;

import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.beacons.BeaconObserverImplementation;
import de.smarthome.beacons.BeaconObserverSubscriber;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.responses.CallBackServiceInput;
import de.smarthome.app.model.responses.CallbackValueInput;
import de.smarthome.app.model.responses.ServiceEvent;
import de.smarthome.server.CallbackSubscriber;
import de.smarthome.server.MyFirebaseMessagingService;

public class Repository implements CallbackSubscriber, BeaconObserverSubscriber {
    private static final String TAG = "Repository";
    private static Repository INSTANCE;

    private ConfigContainer configContainer;
    private ServerCommunicator serverCommunicator;

    private BeaconObserverImplementation beaconObserver;

    //Needed for beaconObserver
    private Application parentApplication;

    //Needed for BeaconDialog in Application, After confirm set null
    private Location beaconLocation = null;
    //Have beacon been updated? Yes ==> Start dialog
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

    public void test(){
        configContainer.fillWithDummyValueAllConfigs();
    }

    public void setSelectedFunction(Function function) {
        configContainer.setSelectedFunction(function);
    }

    public Function getSelectedFunction() {
        return configContainer.getSelectedFunction();
    }

    public Location getSelectedLocation() {
        return configContainer.getSelectedLocation();
    }

    public void setSelectedLocation(Location newLocation) {
        configContainer.setSelectedLocation(newLocation);
    }

    public void initBeaconCheck() {
        beaconCheck.postValue(false);
    }

    public LiveData<Boolean> checkBeacon() {
        return beaconCheck;
    }

    public Location getBeaconLocation() {
        return beaconLocation;
    }

    public LiveData<Boolean> getLoginDataStatus() {
        return serverCommunicator.getLoginRequestStatus();
    }

    public ChannelConfig getSmartHomeChannelConfig() {
        return configContainer.getSmartHomeChannelConfig();
    }

    public void confirmBeacon() {
        setSelectedLocation(beaconLocation);
        beaconLocation = null;
    }

    public void initBeaconObserver() {
        beaconObserver = new BeaconObserverImplementation(parentApplication, parentApplication.getApplicationContext(),
                configContainer.getSmartHomeUiConfig(), configContainer.getSmartHomeBeaconLocations());
        beaconObserver.subscribe(this);
        beaconObserver.init();
        initBeaconCheck();
    }

    public void requestRegisterUser(Credential credential) {
        serverCommunicator.requestRegisterUser(credential);
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
        return configContainer.getFunctionMap();
    }

    public LiveData<Map<Datapoint, BoundaryDataPoint>> getBoundaryMap() {
        return configContainer.getBoundaryMap();
    }

    public MutableLiveData<Map<Datapoint, Datapoint>> getDataPointMap() {
        return configContainer.getDataPointMap();
    }

    public MutableLiveData<Map<String, String>> getStatusUpdateMap() {
        return configContainer.getStatusUpdateMap();
    }

    public MutableLiveData<Map<String, String>> getStatusGetValueMap() {
        return configContainer.getStatusGetValueMap();
    }

    @Override
    public void update(CallbackValueInput input) {
        if (input.getEvent() == null && input.getValue() != null) {
            String value = String.valueOf(input.getValue());
            String uID = input.getUid();
            configContainer.updateStatusUpdateMap(uID, value);
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
                        serverCommunicator.getUIConfigAfterRestart();
                        break;
                    case PROJECT_CONFIG_CHANGED:
                        Log.d(TAG, "PROJECT_CONFIG_CHANGED");
                        serverCommunicator.getAdditionalConfigsAfterRestart();
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
        beaconLocation = newLocation;
        beaconCheck.postValue(true);
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