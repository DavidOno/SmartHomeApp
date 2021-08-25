package de.smarthome.app.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.model.responses.Events;
import de.smarthome.app.repository.responsereactor.ServerConnectionEvent;
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
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.server.CallbackSubscriber;
import de.smarthome.server.NoSSLRestTemplateCreator;
import de.smarthome.server.SmartHomeFMS;
import de.smarthome.server.gira.GiraServerHandler;

/**
 * This class handles the communication of the application with the servers through the servercommunicator class and
 * the storage of the data with the configcontainer class.
 * Additionally it subscribes to the firebasemessagingservice and the beaconobserver and handles their updates.
 */
public class Repository implements CallbackSubscriber, BeaconObserverSubscriber {
    private static final String TAG = "Repository";
    private static Repository INSTANCE;

    private ConfigContainer configContainer;
    private ServerCommunicator serverCommunicator;

    private BeaconObserverImplementation beaconObserver;
    private Application parentApplication;
    private Location beaconLocation = null;
    private final MutableLiveData<Boolean> beaconCheck = new MutableLiveData<>();

    private Repository(){}

    /**
     * Returns the current instance of the repository or creates a new repository
     * @return current instance of the repository
     */
    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
            INSTANCE.configContainer = new ConfigContainer();
            INSTANCE.serverCommunicator = new ServerCommunicator(new GiraServerHandler(new HomeServerCommandInterpreter(new NoSSLRestTemplateCreator())));
        }
        return INSTANCE;
    }

    /**
     * Deletes the current instance of the repository
     */
    public static void destroyInstance(){
        INSTANCE = null;
    }

    public void setParentApplication(Application parentApplication) {
        this.parentApplication = parentApplication;
        serverCommunicator.setParentApplication(parentApplication);
    }

    public void setServerConnectionEvent(ServerConnectionEvent type){
        serverCommunicator.setServerConnectionEvent(type);
    }

    public MutableLiveData<Boolean> getServerConnectionStatus(){
        return serverCommunicator.getServerConnectionStatus();
    }

    /**
     * Restarts failed connection to the servers
     */
    public void retryConnectionToServerAfterFailure(){
       serverCommunicator.setServerConnectionStatus(true);
       serverCommunicator.retryConnectionToServer();
    }

    /**
     * Restarts the connection to both servers
     */
    public void restartConnectionToServer(){
        serverCommunicator.setGiraServerConnectionStatus(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
        serverCommunicator.setCallbackServerConnectionStatus(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
        retryConnectionToServerAfterFailure();
    }

    public void setLoginStatus(boolean update){
        serverCommunicator.setLoginStatus(update);
    }

    public LiveData<Boolean> getLoginStatus() {
        return serverCommunicator.getLoginStatus();
    }

    public void initSelectedFunction(Function function) {
        configContainer.initSelectedFunction(function);
    }

    public Function getSelectedFunction() {
        return configContainer.getSelectedFunction();
    }

    public void initSelectedLocation(Location newLocation) {
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

    /**
     * Initialised the beaconLocation as the selectedLocation and then resets beaconLocation
     */
    public void confirmBeaconLocation() {
        initSelectedLocation(beaconLocation);
        beaconLocation = null;
    }

    /**
     * Initialises the beaconobserver so the repository can get updates
     */
    public synchronized void initBeaconObserver() {
        if(parentApplication != null && configContainer.getUIConfig() != null && configContainer.getBeaconLocations() != null){
            beaconObserver = new BeaconObserverImplementation(parentApplication, parentApplication.getApplicationContext(),
                    configContainer.getUIConfig(), configContainer.getBeaconLocations(),
                    new DefaultBeaconManagerCreator());
            beaconObserver.subscribe(this);
            beaconObserver.init();
        }
        setBeaconCheckFalse();
    }

    /**
     * Starts a connection to the gira server and callbackserver and subscribes the repository to the firebasemessagingservice
     * @param credential User credentials for the registration at gira
     */
    public void requestRegisterUser(Credential credential) {
        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.connectToGira(credential.getId(), credential.getPassword())));
        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.connectToCallbackServer()));
        subscribeToMyFirebaseMessagingService();
    }

    private void subscribeToMyFirebaseMessagingService() {
        SmartHomeFMS.getValueObserver().subscribe(this);
        SmartHomeFMS.getServiceObserver().subscribe(this);
    }

    public void requestSetValue(String id, String value) {
        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestSetValue(id, value)));
    }

    public void requestGetValue(List<String> ids) {
        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestGetValue(ids)));
    }

    public MutableLiveData<List<Location>> getLocationList() {
        return configContainer.getLocationList();
    }

    public MutableLiveData<Map<Function, Function>> getFunctionMap() {
        return configContainer.getFunctionMap();
    }

    /**
     * Requests the current status values of the functions of the selected location or the datapoints of the selected function.
     * Input decides which will be requested.
     * @param inputType Type of the current requests
     */
    public void requestCurrentStatusValues(StatusRequestType inputType){
        List<String> requestList = new ArrayList<>();
        if(inputType == StatusRequestType.FUNCTION
                && configContainer.getFunctionMap().getValue() != null){
                requestList = getFunctionDataPointsForStatusValues(configContainer.getFunctionMap().getValue());
        }else if(inputType == StatusRequestType.DATAPOINT
                && configContainer.getDataPointMap().getValue() != null){
                requestList = requestCurrentDataPointValues(configContainer.getDataPointMap().getValue());
        }
        if(!requestList.isEmpty()){
            requestGetValue(requestList);
        }
    }

    private List<String> getFunctionDataPointsForStatusValues(Map<Function, Function> functionMap){
        List<Function> functionList = new ArrayList<>(functionMap.keySet());
        List<String> requestList = new ArrayList<>();
        if(configContainer.getChannelConfig() != null){
            for(Function func : functionList){
                if(configContainer.getChannelConfig().isFirstDataPointBinary(func)){
                    if(functionMap.get(func) != null){
                        requestList.add(functionMap.get(func).getDataPoints().get(0).getID());
                    }
                }else{
                    if(configContainer.getChannelConfig().hasFirstDataPointOnlyRead(func)){
                        requestList.add(functionMap.get(func).getDataPoints().get(0).getID());
                    }
                }
            }
        }
        return requestList;
    }

    public LiveData<Map<Datapoint, BoundaryDataPoint>> getBoundaryMap() {
        return configContainer.getBoundaryMap();
    }

    public MutableLiveData<Map<Datapoint, Datapoint>> getDataPointMap() {
        return configContainer.getDataPointMap();
    }

    private List<String> requestCurrentDataPointValues(Map<Datapoint, Datapoint> dataPointMap){
        List<Datapoint> dataPointList = new ArrayList<>(dataPointMap.keySet());
        List<String> requestList = new ArrayList<>();
        for(Datapoint dp : dataPointList){
            if(dataPointMap.get(dp) != null && !dataPointMap.get(dp).getID().equals(dp.getID())){
                requestList.add(dataPointMap.get(dp).getID());
            }
        }
        return requestList;
    }

    public MutableLiveData<Map<String, String>> getStatusUpdateMap() {
        return configContainer.getStatusUpdateMap();
    }

    public MutableLiveData<Map<String, String>> getStatusGetValueMap() {
        return configContainer.getStatusGetValueMap();
    }

    public void setStatusGetValueMap(Map<String, String> newStatusValuesMap) {
        configContainer.setStatusGetValueMap(newStatusValuesMap);
    }

    public void initBeaconObserverWithBeaconLocations(BeaconLocations newBeaconLocations){
        configContainer.initBeaconLocations(newBeaconLocations);
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

    /**
     * Handles the updates from the callbackserver, either a status update or an event.
     * @param input Contains the id of the updated datapoint and its value or an event
     */
    @Override
    public void update(CallbackValueInput input) {
        if (input.getEvent() == null && input.getValue() != null) {
            String value = String.valueOf(input.getValue());
            String uID = input.getUid();
            configContainer.initStatusUpdateMap(uID, value);
        }else{
            update(input.getEvent());
        }
    }

    private void update(Events input){
        if (input != null) {
            switch (input) {
                case STARTUP:
                    Log.d(TAG, "Server has started.");
                    serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestSavedCredentialsForLoginAtGira()));
                    break;
                case RESTART:
                    Log.d(TAG, "Server got restarted.");
                    break;
                case UI_CONFIG_CHANGED:
                    Log.d(TAG, "UI_CONFIG_CHANGED");
                    serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestOnlyUIConfig()));
                    break;
                case PROJECT_CONFIG_CHANGED:
                    Log.d(TAG, "PROJECT_CONFIG_CHANGED");
                    serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestOnlyAdditionalConfigs()));
                    break;
                default:
                    Log.d(TAG, "Unknown CallBackServiceInputEvent");
                    break;
            }
        }
    }

    /**
     * Handles the events from the callbackserver.
     * @param input Event send by the callbackserver
     */
    @Override
    public void update(CallBackServiceInput input) {
        if (input.getServiceEvents() != null) {
            for (ServiceEvent serviceEvent : input.getServiceEvents()) {
                switch(serviceEvent.getEvent()) {
                    case STARTUP:
                        Log.d(TAG, "Server has started.");
                        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestSavedCredentialsForLoginAtGira()));
                        break;
                    case RESTART:
                        Log.d(TAG, "Server got restarted.");
                        break;
                    case UI_CONFIG_CHANGED:
                        Log.d(TAG, "UI_CONFIG_CHANGED");
                        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestOnlyUIConfig()));
                        break;
                    case PROJECT_CONFIG_CHANGED:
                        Log.d(TAG, "PROJECT_CONFIG_CHANGED");
                        serverCommunicator.addToExecutorService(new Thread(() -> serverCommunicator.requestOnlyAdditionalConfigs()));
                        break;
                    default:
                        Log.d(TAG, "Unknown CallBackServiceInputEvent");
                        break;
                }
            }
        }
    }

    /**
     * Handles updates from the beaconobserver.
     * Sets new beaconlocation and updates beaconcheck
     * @param newLocation Location that will be the set as beaconlocation
     */
    @Override
    public void update(Location newLocation) {
        if(!newLocation.equals(configContainer.getSelectedLocation())){
            beaconLocation = newLocation;
            beaconCheck.postValue(true);
        }
    }

    /**
     * Unsubscribes and unregisters the application from all services
     */
    public void unsubscribeFromEverything() {
        Thread unsubscribeFromEverythingThread = new Thread(() -> {
            try {
                SmartHomeFMS.getValueObserver().unsubscribe(this);
                SmartHomeFMS.getServiceObserver().unsubscribe(this);
                serverCommunicator.unregisterFromServers();
                beaconObserver.unsubscribe();
            }catch (Exception e){
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        });
        serverCommunicator.addToExecutorService(unsubscribeFromEverythingThread);
    }
}