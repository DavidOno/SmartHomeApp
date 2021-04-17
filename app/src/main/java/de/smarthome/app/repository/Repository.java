package de.smarthome.app.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.credentials.Credential;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.smarthome.SmartHomeApplication;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.beacons.BeaconObserverImplementation;
import de.smarthome.beacons.BeaconObserverSubscriber;
import de.smarthome.command.AdditionalConfigs;
import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.AdditionalConfigCommand;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.MultiReactorCommandChainImpl;
import de.smarthome.command.impl.RegisterCallback;
import de.smarthome.command.impl.RegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.RegisterClientCommand;
import de.smarthome.command.impl.ResponseReactorBeaconConfig;
import de.smarthome.command.impl.ResponseReactorCallbackServer;
import de.smarthome.command.impl.ResponseReactorChannelConfig;
import de.smarthome.command.impl.ResponseReactorCheckAvailability;
import de.smarthome.command.impl.ResponseReactorClient;
import de.smarthome.command.impl.ResponseReactorGiraCallbackServer;
import de.smarthome.command.impl.ResponseReactorUIConfig;
import de.smarthome.command.impl.SingleReactorCommandChainImpl;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.command.impl.UnRegisterCallback;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.app.model.configs.Channel;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.configs.ChannelDatapoint;
import de.smarthome.app.model.impl.Datapoint;
import de.smarthome.app.model.impl.Function;
import de.smarthome.app.model.impl.Location;
import de.smarthome.app.model.impl.UIConfig;
import de.smarthome.app.model.responses.CallBackServiceInput;
import de.smarthome.app.model.responses.CallbackValueInput;
import de.smarthome.app.model.responses.ServiceEvent;
import de.smarthome.server.CallbackSubscriber;
import de.smarthome.server.MyFirebaseMessagingService;
import de.smarthome.server.ServerHandler;
import de.smarthome.server.gira.GiraServerHandler;
import de.smarthome.app.utility.ToastUtility;

public class Repository implements CallbackSubscriber, BeaconObserverSubscriber {
    private  final String TAG = "Repository";
    private static Repository instance;
    private final ToastUtility toastUtility = ToastUtility.getInstance();

    private final ServerHandler serverHandler = new GiraServerHandler(new HomeServerCommandInterpreter());
    private BeaconObserverImplementation beaconObserver;
    private Application parentApplication;

    private UIConfig smartHomeUiConfig;
    private ChannelConfig smartHomeChannelConfig;
    private BeaconLocations smartHomeBeaconLocations;
    //private BoundariesConfig smartHomeBoundariesConfig;

    private Credential userCredential;

    private Location selectedLocation;
    //Used to check type of ChannelDataPoint
    private Function selectedFunction;

    private static final String IP_OF_CALLBACK_SERVER = "192.168.132.213:8443";

    private MutableLiveData<List<Location>> locationList = new MutableLiveData<>();
    private MutableLiveData<Map<Function, Function>> functionList = new MutableLiveData<>();
    private MutableLiveData<Map<Datapoint, Datapoint>> dataPointList = new MutableLiveData<>();

    private MutableLiveData<Map<String, String>> statusList = new MutableLiveData<>();

    private MutableLiveData<Boolean> beaconCheck = new MutableLiveData<>();

    //TODO: Can the Repo get delete while app is active? if not, create first Repo in Activity so the Fragment do not need to give Application
    public static Repository getInstance(@Nullable Application application) {
        if (instance == null) {
            instance = new Repository();

            instance.parentApplication = application;

            //TODO: Just for testing purposes! Has to be removed before product is finished!
            instance.fillWithDummyValueAllConfigs();
        }
        return instance;
    }

    private void fillWithDummyValueAllConfigs(){
        fillWithDummyValuesUIConfig();
        fillWithDummyValuesChannelConfig();
        fillWithDummyValueBeaconConfig();
    }

    public void setSelectedFunction(Function function){
        this.selectedFunction = function;
        updateDataPointList(function);
    }

    public Credential getUserCredential(){
        return userCredential;
    }

    public Function getSelectedFunction() {
        return selectedFunction;
    }

    public void initBeaconCheck(){
        beaconCheck.postValue(false);
    }

    public LiveData<Boolean> checkBeacon(){
        return beaconCheck;
    }

    public ChannelConfig getSmartHomeChannelConfig(){
        return smartHomeChannelConfig;
    }

    private void initBeaconObserver(){
        beaconObserver = new BeaconObserverImplementation(parentApplication,
                parentApplication.getApplicationContext(),
                smartHomeUiConfig, smartHomeBeaconLocations);
        beaconObserver.subscribe(this);
        beaconObserver.init();

        initBeaconCheck();
    }

    public void setSelectedLocation(Location newLocation) {
        selectedLocation = newLocation;
        updateFunctionList(selectedLocation);
    }

    private void addToExecutorService(Thread newThread) {
        SmartHomeApplication.EXECUTOR_SERVICE.execute(newThread);
    }

    public void requestCheckAvailability() {
        Thread requestCheckAvailabilityThread = new Thread(() -> {
            Command availability = new CheckAvailabilityCommand();
            handleResponseEntities(serverHandler.sendRequest(availability));
        });
        addToExecutorService(requestCheckAvailabilityThread);
    }

    public void requestRegisterUser(Credential credential) {
        userCredential = credential;

        initialisationOfApplication(userCredential.getId(), userCredential.getPassword());
    }

    private void initialisationOfApplication(String userName, String pwd) {
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();

        registerAppAtGiraServer(userName, pwd, multiCommandChain);
        getAllConfigs(multiCommandChain);

        serverHandler.sendRequest(multiCommandChain);

        //TODO: Check if this position is best. Worked by Stefan but maybe this need to be moved!
        MyFirebaseMessagingService.getValueObserver().subscribe(this);
    }

    private void getAllConfigs(MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new UIConfigCommand(), new ResponseReactorUIConfig(this));

        getAdditionalConfigs(multiCommandChain);
    }

    private void getAdditionalConfigs(MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.CHANNEL), new ResponseReactorChannelConfig(this));
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.LOCATION), new ResponseReactorBeaconConfig(this));
        //multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.BOUNDARIES), new ResponseReactorBoundariesConfig(this));
    }

    private void registerAppAtGiraServer(String userName, String pwd, MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new RegisterClientCommand(userName, pwd), new ResponseReactorClient());
        multiCommandChain.add(new CheckAvailabilityCommand(), new ResponseReactorCheckAvailability());
        multiCommandChain.add(new RegisterCallbackServerAtGiraServer(IP_OF_CALLBACK_SERVER), new ResponseReactorGiraCallbackServer());
        multiCommandChain.add(new RegisterCallback(IP_OF_CALLBACK_SERVER), new ResponseReactorCallbackServer());
    }

    public void setUIConfig(UIConfig newUiconfig){
        smartHomeUiConfig = newUiconfig;
        initParentLocation(smartHomeUiConfig);
        updateLocationList();

        if(selectedLocation != null){
            checkCurrentSelectedLocation();
        }
        if(selectedFunction != null){
            checkCurrentSelectedFunction();
        }
    }

    private void checkCurrentSelectedLocation(){
        //TODO: Check if this works!
        String currentLocationName = selectedLocation.getName();

        for(Location loc : locationList.getValue()){
            if(loc.getName().equals(currentLocationName)){
                setSelectedLocation(loc);
                break;
            }
        }
        toastUtility.prepareToast("Current Location was not fund in new UIConfig!");
    }

    private void checkCurrentSelectedFunction(){
        //TODO: Check if this works!
        String currentFunctionName = selectedFunction.getName();

        for(Function func : selectedLocation.getFunctions(smartHomeUiConfig)){
            if(func.getName().equals(currentFunctionName)){
                setSelectedFunction(func);
                break;
            }
        }
        toastUtility.prepareToast("Selected Function was not fund in new UIConfig!");
    }

    public void setChannelConfig(ChannelConfig newChannelconfig){
        smartHomeChannelConfig = newChannelconfig;
    }

    public void setBeaconConfig(BeaconLocations newBeaconConfig){
        smartHomeBeaconLocations = newBeaconConfig;
        initBeaconObserver();
    }


    public void requestRegisterClient(String ipOfServer){
        Thread requestRegisterClientThread = new Thread(() -> {
            AsyncCommand register = new RegisterCallback(ipOfServer);
            serverHandler.sendRequest(register);
        });
        addToExecutorService(requestRegisterClientThread);
    }



    public void requestUnregisterClient(String ipOfServer){
        Thread requestUnregisterClientThread = new Thread(() -> {
            AsyncCommand register = new UnRegisterCallback(ipOfServer);
            serverHandler.sendRequest(register);
        });
        addToExecutorService(requestUnregisterClientThread);
    }


    public void requestGetConfig(){
        Thread requestGetConfigThread = new Thread(() -> {
            Command getConfig = new UIConfigCommand();
            serverHandler.sendRequest(getConfig);
        });

        addToExecutorService(requestGetConfigThread);
    }


    public void requestSetValue(String ID, String value){
        Thread requestSetValueThread = new Thread(() -> {
            Command setValueCommand = new ChangeValueCommand(ID, Float.parseFloat(value));
            serverHandler.sendRequest(setValueCommand);
        });

        addToExecutorService(requestSetValueThread);
    }

    public void requestGetValue(String ID){
        Thread requestGetValueThread = new Thread(() -> {
            Command getValueCommand = new GetValueCommand(ID);
            serverHandler.sendRequest(getValueCommand);
        });

        addToExecutorService(requestGetValueThread);
    }

    public void requestRegisterCallbackServerAtGiraServer(String ipOfServer){
        Thread requestRegisterCallbackServerAtGiraServerThread = new Thread(() -> {
            Command registerAtGira = new RegisterCallbackServerAtGiraServer(ipOfServer);
            serverHandler.sendRequest(registerAtGira);
        });
        addToExecutorService(requestRegisterCallbackServerAtGiraServerThread);
    }


    public void requestUnRegisterCallbackServerAtGiraServer(){
        Thread requestUnRegisterCallbackServerAtGiraServerThread = new Thread(() -> {
            Command unregisterAtGira = new UnRegisterCallbackServerAtGiraServer();
            serverHandler.sendRequest(unregisterAtGira);
        });

        addToExecutorService(requestUnRegisterCallbackServerAtGiraServerThread);
    }

    //public void requestGetValues(Location location, UIConfig uiConfig)

    public MutableLiveData<List<Location>> getRooms(){
        return locationList;
    }

    public void updateLocationList(){
        List<Location> allLocations = new ArrayList<>();
        allLocations.addAll(smartHomeUiConfig.getLocations());

        for(Location location: smartHomeUiConfig.getLocations()){
            getAllChildrenFromLocation(location, allLocations);
        }

        locationList.postValue(allLocations);
    }

    public void initParentLocation(UIConfig uiConfig){
        for(Location loc: uiConfig.getLocations()){
            loc.initParentLocation(loc);
        }
    }

    public void getAllChildrenFromLocation(Location location, List<Location> resultList){
        for(Location loc: location.getLocations()){
            resultList.add(loc);
            getAllChildrenFromLocation(loc, resultList);
        }
    }

    public MutableLiveData<Map<Function, Function>> getRoomUsableFunctions(){
        return functionList;
    }


    public void updateFunctionList(Location viewedLocation){
        Map<Function, Function> completeFunctionMap = new LinkedHashMap<Function, Function>();

        mapStatusFunctionToFunction(completeFunctionMap, viewedLocation);

        if(!viewedLocation.equals(viewedLocation.getParentLocation())){
            mapStatusFunctionToFunction(completeFunctionMap, viewedLocation.getParentLocation());
        }

        functionList.postValue(completeFunctionMap);
    }

    private void mapStatusFunctionToFunction(Map<Function, Function> completeFunctionMap, Location parentLocation) {
        for (Function func : parentLocation.getFunctions(smartHomeUiConfig)) {
            Function functionStatus = null;
            if (!func.isStatusFunction()) {
                for (Function comparedFunction : parentLocation.getFunctions(smartHomeUiConfig)) {
                    if (comparedFunction.isStatusFunction()) {
                        if (func.getName().split("_")[0].equals(
                                comparedFunction.getName().split("_")[0])) {
                            functionStatus = comparedFunction;
                            break;
                        }
                    }
                }
                completeFunctionMap.put(func, functionStatus);
            }
        }
    }

    public MutableLiveData<Map<Function, Function>> getRoomStatusFunctions(){
        return functionList;
    }


    public void updateDataPointList(Function function){
        Map<Datapoint, Datapoint> newValue = new LinkedHashMap<>();

        //TODO: Check if StatusFunction always has the same number of Datapoint than "normal" Function
        if(functionList.getValue().get(function) != null){
            for(int i = 0; i < function.getDataPoints().size(); i++){
                newValue.put(function.getDataPoints().get(i), functionList.getValue().get(function).getDataPoints().get(i));
            }
        }else{
            for(Datapoint datapoint : function.getDataPoints()){
                newValue.put(datapoint, null);
            }
        }

        dataPointList.postValue(newValue);
    }


    public MutableLiveData<Map<Datapoint, Datapoint>> getDataPoints(){
        return dataPointList;
    }


    public void updateStatusList(String functionUID, String value){
        Map<String, String> newValue = new HashMap<>();
        newValue.put(functionUID, value);

        statusList.postValue(newValue);
    }

    public MutableLiveData<Map<String, String>> getStatusList(){
        return statusList;
    }

    public Function getFunctionByUID(String UID, Location location){
        for(Function function : location.getFunctions(smartHomeUiConfig)){
            if(function.getID().equals(UID)){
                return function;
            }
        }
        return null;
    }

    public void handleResponseEntities(ResponseEntity responseEntity){
        try {
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    System.out.println("response received");
                    System.out.println(responseEntity.getBody());
                } else {
                    System.out.println("error occurred");
                    System.out.println(responseEntity.getStatusCode());
                }
        }catch(Exception e){
            Log.d(TAG, "handleResponseEntities, Exerptíon: " + e.toString());
        }
    }


    @Override
    public void update(CallbackValueInput input) {
        //CallbackValueInput test = new CallbackValueInput(0, "0", null, null,"uiconfigchanged");
        //input = test;
        if(input.getEvent() != null){
            switch(input.getEvent()){
                case TEST:
                    Log.d(TAG, "Test Event");
                    System.out.println("Test Event");
                    break;

                case STARTUP:
                    Log.d(TAG, "Server has started.");
                    System.out.println("Server has started.");

                    initialisationOfApplication(userCredential.getId(), userCredential.getPassword());
                    break;

                case RESTART:
                    Log.d(TAG, "Server got restarted.");
                    System.out.println("Server got restarted.");
                    break;

                case UI_CONFIG_CHANGED:
                    Log.d(TAG, "UI_CONFIG_CHANGED");
                    System.out.println("UI_CONFIG_CHANGED");

                    SingleReactorCommandChainImpl singleCommandChain = new SingleReactorCommandChainImpl(new ResponseReactorUIConfig(this));
                    singleCommandChain.add(new UIConfigCommand());
                    serverHandler.sendRequest(singleCommandChain);
                    break;

                case PROJECT_CONFIG_CHANGED:
                    Log.d(TAG, "PROJECT_CONFIG_CHANGED");
                    System.out.println("PROJECT_CONFIG_CHANGED");

                    MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
                    getAdditionalConfigs(multiCommandChain);
                    serverHandler.sendRequest(multiCommandChain);

                    break;

                default:
                    Log.d(TAG, "Unknown CallbackValueInputEvent");
                    System.out.println("Unknown CallbackValueInputEvent");
                    break;
            }

        }else if(input.getValue() != null){
            String value = String.valueOf(input.getValue());
            String uID = input.getUid();

            updateStatusList(uID, value);
        }
    }

    @Override
    public void update(CallBackServiceInput input) {
            for(ServiceEvent serviceEvent: input.getServiceEvents()){
                switch(serviceEvent.getEvent()){
                    case STARTUP:
                        Log.d(TAG, "Server has started.");
                        System.out.println("Server has started.");

                        initialisationOfApplication(userCredential.getId(), userCredential.getPassword());
                        break;

                    case RESTART:
                        Log.d(TAG, "Server got restarted.");
                        System.out.println("Server got restarted.");
                        break;

                    case UI_CONFIG_CHANGED:
                        Log.d(TAG, "UI_CONFIG_CHANGED");
                        System.out.println("UI_CONFIG_CHANGED");

                        SingleReactorCommandChainImpl singleCommandChain = new SingleReactorCommandChainImpl(new ResponseReactorUIConfig(this));
                        singleCommandChain.add(new UIConfigCommand());
                        serverHandler.sendRequest(singleCommandChain);

                        break;

                    case PROJECT_CONFIG_CHANGED:
                        Log.d(TAG, "UI_CONFIG_CHANGED");
                        System.out.println("UI_CONFIG_CHANGED");

                        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
                        getAdditionalConfigs(multiCommandChain);
                        serverHandler.sendRequest(multiCommandChain);

                        break;

                    default:
                        Log.d(TAG, "Unknown CallBackServiceInputEvent");
                        System.out.println("Unknown CallBackServiceInputEvent");
                        break;
                }
            }
    }


    @Override
    public void update(Location newLocation) {
        setSelectedLocation(newLocation);
        beaconCheck.postValue(true);
    }


    private Location createEssen(){
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aael");
        functionIDs.add("aae4");

        functionIDs.add("aae8");
        functionIDs.add("aafe");
        List<Location> loc = new ArrayList<>();

        return new Location("Bereich Essen","aaej","4", functionIDs, loc,"Room");
    }
    private Location createWohnen(){
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aaet");
        functionIDs.add("aae2");
        functionIDs.add("aae8");

        functionIDs.add("aael");
        functionIDs.add("aaafe");

        List<Location> loc = new ArrayList<>();

        return new Location("Bereich Wohnen","aaex","4", functionIDs, loc,"Room");
    }
    private Location createTerrasse(){
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aafe");
        functionIDs.add("aafg");

        functionIDs.add("aael");
        functionIDs.add("aae8");

        List<Location> loc = new ArrayList<>();

        return new Location("Terrasse","aafb","4", functionIDs, loc,"Room");
    }

    private void fillWithDummyValuesUIConfig(){
        Thread thread = new Thread(() -> {
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

            UIConfig newUiConfig =  new UIConfig(funcList1, uiconfigloc2,"cczk");
            //Log.d("Hello", "New UIConfig " + newUiConfig.toString());
            setUIConfig(newUiConfig);
        });
        addToExecutorService(thread);
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
                "\t\t\t\"roomUID\": \"aafk\",\n" +
                "\t\t\t\"beaconId\": \"11111111-11a1-1111-1111-1a111111111100\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aaej\",\n" +
                "\t\t\t\"beaconId\": \"7b44b47b-52a1-5381-90c2-f09b6838c5d400\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        try {
            setBeaconConfig(mapper.readValue(locationConfigString, new TypeReference<BeaconLocations>() {}));

        }catch(Exception e){
            Log.d(TAG, "BeaconConfig Exception " + e.toString());
        }
    }
}
