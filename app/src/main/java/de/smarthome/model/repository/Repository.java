package de.smarthome.model.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.smarthome.SmartHomeApplication;
import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.CommandChainImpl;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.RegisterCallback;
import de.smarthome.command.impl.RegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.RegisterClientCommand;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.command.impl.UnRegisterCallback;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.model.configs.Channel;
import de.smarthome.model.configs.ChannelConfig;
import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.model.impl.Location;
import de.smarthome.model.impl.UIConfig;
import de.smarthome.model.responses.CallBackServiceInput;
import de.smarthome.model.responses.CallbackValueInput;
import de.smarthome.server.CallbackSubscriber;
import de.smarthome.server.MyFirebaseMessagingService;
import de.smarthome.server.ServerHandler;
import de.smarthome.server.gira.GiraServerHandler;

public class Repository implements CallbackSubscriber {
    private  final String TAG = "Repository";
    private Map<Location, List<Function>> roomIDToIDs = new HashMap<>();
    private static Repository instance;
    private final ServerHandler serverHandler = new GiraServerHandler(new HomeServerCommandInterpreter());
    private MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();

    private UIConfig uiConfig;
    private ChannelConfig channelConfig;

    private Location selectedLocation;

    private static final String ipOfCallbackServer = "192.168.132.211:8443";

    //private CallbackSubscriber callbackSubscriber;

    public Repository(){
        System.out.println("\t\t\t\tTHREAD::::"+Thread.currentThread().getName());
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();

            instance.initialiseRepository();

            instance.getNewUIConfig();
            instance.getNewChannelConfig();

        }
        return instance;
    }

    private void getNewUIConfig() {

        fillWithDummyValuesUIConfig();
    }

    private void getNewChannelConfig(){
        fillWithDummyValuesChannelConfig();
    }


    public Map<Location, List<Function>> getRoomIDToIDs() {
        return roomIDToIDs;
    }

    public void setRoomIDToIDs(Map<Location, List<Function>> roomIDToIDs) {
        this.roomIDToIDs = roomIDToIDs;
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Location selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void initialiseRepository(){
        CommandChainImpl commandChain = new CommandChainImpl();
        commandChain.add(new CheckAvailabilityCommand());
        commandChain.add(new RegisterCallbackServerAtGiraServer(ipOfCallbackServer));
        commandChain.add(new RegisterCallback(ipOfCallbackServer));

        //handleResponseEntities(serverHandler.sendRequest(commandChain));

        myFirebaseMessagingService.getValueObserver().subscribe(this); //TODO: handle MyFirebaseMessagingService
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

    public void requestRegisterUser(String userName, String pwd) {
        Thread requestRegisterUserThread = new Thread(() -> {
            Command registerClient = new RegisterClientCommand(userName, pwd);
            serverHandler.sendRequest(registerClient);
        });
        addToExecutorService(requestRegisterUserThread);
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
            Command setValueCommand = new ChangeValueCommand("aauy", Integer.parseInt(value));
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
        MutableLiveData<List<Location>> data = new MutableLiveData<>();

        data.setValue(uiConfig.getLocations());
        return data;
    }

    public MutableLiveData<List<Function>> getRoomUsableFunctions(){
        MutableLiveData<List<Function>> data = new MutableLiveData<>();
        List<Function> usableFunctionsList = new ArrayList<>();

        for(Function func : selectedLocation.getFunctions(uiConfig)){
            if(!func.isStatusFunction()){
                usableFunctionsList.add(func);
            }
        }
        data.setValue(usableFunctionsList);

        return data;
    }

    public MutableLiveData<List<Function>> getRoomStatusFunctions(){
        MutableLiveData<List<Function>> data = new MutableLiveData<>();
        List<Function> statusFunctionsList = new ArrayList<>();

        for(Function func : selectedLocation.getFunctions(uiConfig)){
            if(func.isStatusFunction()){
                statusFunctionsList.add(func);
            }
        }
        data.setValue(statusFunctionsList);

        return data;
    }

    public Function getFunctionByUID(String UID){
        for(Function function : selectedLocation.getFunctions(uiConfig)){
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
            Log.d(TAG, "Response handle exception: " + e.toString());
        }
    }

    @Override
    public void update(CallbackValueInput input) {
        Log.d(TAG, "Hello " + input.toString());
        System.out.println("Hello " + input.toString());
    }

    @Override
    public void update(CallBackServiceInput input) {

    }

    public Location createEssen(){
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aael");
        functionIDs.add("aae4");

        functionIDs.add("aae8");
        functionIDs.add("aafe");
        List<Location> loc = new ArrayList<>();

        return new Location("Bereich Essen","aaej","4", functionIDs, loc,"Room");
    }
    public Location createWohnen(){
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aaet");
        functionIDs.add("aae2");
        functionIDs.add("aae8");

        functionIDs.add("aael");
        functionIDs.add("aaafe");

        List<Location> loc = new ArrayList<>();

        return new Location("Bereich Wohnen","aaex","4", functionIDs, loc,"Room");
    }
    public Location createTerrasse(){
        List<String> functionIDs = new ArrayList<>();
        functionIDs.add("aafe");
        functionIDs.add("aafg");

        functionIDs.add("aael");
        functionIDs.add("aae8");

        List<Location> loc = new ArrayList<>();

        return new Location("Terrasse","aafb","4", functionIDs, loc,"Room");
    }

    public void fillWithDummyValuesUIConfig(){
        Thread thread = new Thread(() -> {
            ArrayList<Function> funcList1 = new ArrayList<>();
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
            dataPoints5.add(new Datapoint("aajf", "Set-Point"));
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

            List<Location> uiconfigloc = new ArrayList<>();


            uiconfigloc.add(createEssen());
            uiconfigloc.add(createWohnen());
            uiconfigloc.add(createTerrasse());

            String outout = new UIConfig(funcList1, uiconfigloc,"cczk").toString();
            //Log.d(TAG, outout);

            uiConfig =  new UIConfig(funcList1, uiconfigloc,"cczk");
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
            channelDatapoints.clear();


            channelDatapoints.add(new ChannelDatapoint("Current", "Float", "re"));
            channelDatapoints.add(new ChannelDatapoint("Set-Point", "Float", "rwe"));
            channelDatapoints.add(new ChannelDatapoint("OnOff", "Binary", "rwe"));
            Channel c2 = new Channel("de.gira.schema.channels.RoomTemperatureSwitchable", channelDatapoints);
            channelList.add(c2);
            channelDatapoints.clear();

            channelDatapoints.add(new ChannelDatapoint("Step-Up-Down", "Binary", "w"));
            channelDatapoints.add(new ChannelDatapoint("Up-Down", "Binary", "w"));
            channelDatapoints.add(new ChannelDatapoint("Movement", "Binary", "re"));
            channelDatapoints.add(new ChannelDatapoint("Position", "Percent", "rwe"));
            channelDatapoints.add(new ChannelDatapoint("Slat-Position", "Percent", "rwe"));
            Channel c3 = new Channel("de.gira.schema.channels.RoomTemperatureSwitchable", channelDatapoints);
            channelList.add(c3);

            ChannelConfig output = new ChannelConfig(channelList);
            //Log.d(TAG, output.toString());
            channelConfig = output;
        }).start();
    }
}
