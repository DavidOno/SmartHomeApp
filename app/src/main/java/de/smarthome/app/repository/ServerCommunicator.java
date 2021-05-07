package de.smarthome.app.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.smarthome.SmartHomeApplication;
import de.smarthome.app.model.responses.GetValueReponse;
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
import de.smarthome.command.impl.ResponseReactorBoundariesConfig;
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
import de.smarthome.server.ServerHandler;
import de.smarthome.server.gira.GiraServerHandler;

public class ServerCommunicator {
    private final String TAG = "ServerCommunicator";
    private static ServerCommunicator instance;

    //TODO: Move server stuff into own class to make david happy repository only contains livedata and will be handed to new class in constructor
    private static final String IP_OF_CALLBACK_SERVER = "192.168.132.219:8443";
    private final ServerHandler serverHandler = new GiraServerHandler(new HomeServerCommandInterpreter());

    //Needed for Relogin after Update/Delete of Logindata in Optionfragment
    private Credential userCredential;
    private MutableLiveData<Boolean> loginDataStatus = new MutableLiveData<>();

    private int statusListSize = 0;
    public Map<String, String> statusMapNewValues = new LinkedHashMap<>();

    public static ServerCommunicator getInstance() {
        if (instance == null) {
            instance = new ServerCommunicator();
        }
        return instance;
    }

    public void updateLoginDataStatus(Boolean status) {
        loginDataStatus.postValue(status);
    }

    public LiveData<Boolean> getLoginDataStatus() {
        return loginDataStatus;
    }

    public Credential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(Credential userCredential) {
        this.userCredential = userCredential;
    }

    private void addToExecutorService(Thread newThread) {
        SmartHomeApplication.EXECUTOR_SERVICE.execute(newThread);
    }

    public void requestRegisterUser(Credential credential) {
        setUserCredential(credential);
        initialisationOfApplication(userCredential.getId(), userCredential.getPassword());
    }

    private void initialisationOfApplication(String userName, String pwd) {
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
        registerAppAtGiraServer(userName, pwd, multiCommandChain);
        getAllConfigs(multiCommandChain);
        serverHandler.sendRequest(multiCommandChain);
    }

    public void initialisationOfApplicationAfterRestart() {
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
        registerAppAtGiraServerAfterRestart(userCredential.getId(), userCredential.getPassword(), multiCommandChain);
        serverHandler.sendRequest(multiCommandChain);
    }

    private void getAllConfigs(MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new UIConfigCommand(), new ResponseReactorUIConfig());
        getAdditionalConfigs(multiCommandChain);
    }

    private void getAdditionalConfigs(MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.CHANNEL), new ResponseReactorChannelConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.LOCATION), new ResponseReactorBeaconConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.BOUNDARIES), new ResponseReactorBoundariesConfig());
    }

    public void getUIConfigAfterRestart(){
        SingleReactorCommandChainImpl singleCommandChain = new SingleReactorCommandChainImpl(new ResponseReactorUIConfig());
        singleCommandChain.add(new UIConfigCommand());
        serverHandler.sendRequest(singleCommandChain);
    }

    public void getAdditionalConfigsAfterRestart() {
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.CHANNEL), new ResponseReactorChannelConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.LOCATION), new ResponseReactorBeaconConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfigs.BOUNDARIES), new ResponseReactorBoundariesConfig());
        serverHandler.sendRequest(multiCommandChain);
    }

    private void registerAppAtGiraServer(String userName, String pwd, MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new RegisterClientCommand(userName, pwd), new ResponseReactorClient());
        multiCommandChain.add(new CheckAvailabilityCommand(), new ResponseReactorCheckAvailability());
        multiCommandChain.add(new RegisterCallbackServerAtGiraServer(IP_OF_CALLBACK_SERVER), new ResponseReactorGiraCallbackServer());
        multiCommandChain.add(new RegisterCallback(IP_OF_CALLBACK_SERVER), new ResponseReactorCallbackServer());
    }

    private void registerAppAtGiraServerAfterRestart(String userName, String pwd, MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new RegisterClientCommand(userName, pwd), new ResponseReactorClient());
        multiCommandChain.add(new RegisterCallbackServerAtGiraServer(IP_OF_CALLBACK_SERVER), new ResponseReactorGiraCallbackServer());
    }

    public void requestUnregisterClient(String ipOfServer) {
        Thread requestUnregisterClientThread = new Thread(() -> {
            AsyncCommand register = new UnRegisterCallback(ipOfServer);
            serverHandler.sendRequest(register);
        });
        addToExecutorService(requestUnregisterClientThread);
    }

    public void requestSetValue(String ID, String value) {
        Thread requestSetValueThread = new Thread(() -> {
            Command setValueCommand = new ChangeValueCommand(ID, Float.parseFloat(value));
            serverHandler.sendRequest(setValueCommand);
        });
        addToExecutorService(requestSetValueThread);
    }

    public void requestGetValue(List<String> IDs) {
        statusListSize = IDs.size();
        statusMapNewValues.clear();
        for(String ID :IDs){
            Thread requestGetValueThread = new Thread(() -> {
                Command getValueCommand = new GetValueCommand(ID);
                handleResponseGetValue(serverHandler.sendRequest(getValueCommand));
            });
            addToExecutorService(requestGetValueThread);
        }
    }

    public void requestUnRegisterCallbackServerAtGiraServer() {
        Thread requestUnRegisterCallbackServerAtGiraServerThread = new Thread(() -> {
            Command unregisterAtGira = new UnRegisterCallbackServerAtGiraServer();
            serverHandler.sendRequest(unregisterAtGira);
        });
        addToExecutorService(requestUnRegisterCallbackServerAtGiraServerThread);
    }

    public void handleResponseGetValue(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received");
                System.out.println(responseEntity.getBody());
                GetValueReponse valueReponse = (GetValueReponse) responseEntity.getBody();

                String value = valueReponse.getValues().get(0).getValue();
                String uID = valueReponse.getValues().get(0).getUid();

                statusMapNewValues.put(uID, value);
                if(statusListSize == statusMapNewValues.size()) {
                    statusMapNewValues.put(uID, value);
                    ConfigContainer.getInstance().updateStatusList2(statusMapNewValues);
                }
            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            Log.d(TAG, "handleResponseEntities, Exception: " + e.toString());
        }
    }

    public void unsubscribeFromEverything() {
        requestUnregisterClient(IP_OF_CALLBACK_SERVER);
        requestUnRegisterCallbackServerAtGiraServer();
    }
}