package de.smarthome.app.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.smarthome.SmartHomeApplication;
import de.smarthome.app.model.responses.GetValueResponse;
import de.smarthome.app.repository.responsereactor.ResponseReactorBeaconLocations;
import de.smarthome.app.repository.responsereactor.ResponseReactorBoundariesConfig;
import de.smarthome.app.repository.responsereactor.ResponseReactorCallbackServer;
import de.smarthome.app.repository.responsereactor.ResponseReactorChannelConfig;
import de.smarthome.app.repository.responsereactor.ResponseReactorClient;
import de.smarthome.app.repository.responsereactor.ResponseReactorGiraCallbackServer;
import de.smarthome.app.repository.responsereactor.ResponseReactorUIConfig;
import de.smarthome.app.repository.responsereactor.ServerConnectionEvent;
import de.smarthome.app.utility.ToastUtility;
import de.smarthome.app.model.configs.AdditionalConfig;
import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.impl.AdditionalConfigCommand;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.MultiReactorCommandChainImpl;
import de.smarthome.command.impl.RegisterCallback;
import de.smarthome.command.impl.RegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.RegisterClientCommand;
import de.smarthome.command.impl.SingleReactorCommandChainImpl;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.command.impl.UnRegisterAtCallbackServer;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.server.ServerHandler;

/**
 * This class sends the requests of the repository to the gira and callbackserver.
 * All of the results are handled by responsereactors classes, except in case of the getvalue request.
 */
public class ServerCommunicator {
    private static final String TAG = "ServerCommunicator";
    private final ToastUtility toastUtility = ToastUtility.getInstance();

    private static final String IP_OF_CALLBACK_SERVER = "192.168.132.222:9090";
    private final ServerHandler serverHandler;

    private final MutableLiveData<Boolean> requestStatusLoginUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> serverConnectionStatus = new MutableLiveData<>();
    private ServerConnectionEvent callbackServerConnectionStatus;
    private ServerConnectionEvent giraServerConnectionStatus;

    private Application parentApplication = null;
    private int statusListSize = 0;
    private final Map<String, String> newStatusValuesMap = new LinkedHashMap<>();

    public ServerCommunicator(@NonNull ServerHandler serverHandler){
        this.serverHandler = serverHandler;
    }

    public void setParentApplication(Application parentApplication) {
        this.parentApplication = parentApplication;
    }

    public void addToExecutorService(Thread newThread) {
        SmartHomeApplication.EXECUTOR_SERVICE.execute(newThread);
    }

    /**
     * Depending on the given event sets the serverConnectionStatus and
     * giraServerConnectionStatus or callbackServerConnectionStatus.
     * @param event Event that happened in the last connection attempt
     */
    public void setServerConnectionEvent(ServerConnectionEvent event){
        switch(event){
            case CALLBACK_CONNECTION_FAIL:
                if(callbackServerConnectionStatus != ServerConnectionEvent.CALLBACK_CONNECTION_FAIL){
                    setCallbackServerConnectionStatus(event);
                    setServerConnectionStatus(false);
                }
                break;
            case CALLBACK_CONNECTION_SUCCESS:
                if(callbackServerConnectionStatus != ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS){
                    toastUtility.prepareToast("Successfully connected to CallbackServer.");
                    setCallbackServerConnectionStatus(event);
                    setServerConnectionStatus(true);
                }
                break;
            case GIRA_CONNECTION_FAIL:
                if(giraServerConnectionStatus != ServerConnectionEvent.GIRA_CONNECTION_FAIL){
                    setGiraServerConnectionStatus(event);
                    setServerConnectionStatus(false);
                }
                break;
            case GIRA_CONNECTION_SUCCESS:
                if(giraServerConnectionStatus != ServerConnectionEvent.GIRA_CONNECTION_SUCCESS){
                    toastUtility.prepareToast("Successfully connected to Gira.");
                    setGiraServerConnectionStatus(event);
                    setServerConnectionStatus(true);
                }
                break;
        }
    }

    /**
     * Depending on connection status restarts connection to the servers.
     */
    public void retryConnectionToServer(){
        if(giraServerConnectionStatus == ServerConnectionEvent.GIRA_CONNECTION_FAIL){
            addToExecutorService(new Thread(this::requestSavedCredentialsForLoginAtGira));
        }
        if(callbackServerConnectionStatus == ServerConnectionEvent.CALLBACK_CONNECTION_FAIL){
            addToExecutorService(new Thread(this::connectToCallbackServer));
        }
    }

    /**
     * Establishes a connection to the gira server.
     * @param userName Username that will be used for registration at gira
     * @param pwd Password that will be used for registration at gira
     */
    public void connectToGira(String userName, String pwd){
        setGiraServerConnectionStatus(ServerConnectionEvent.GIRA_CONNECTION_ACTIVE);
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
        multiCommandChain.add(new RegisterClientCommand(userName, pwd), new ResponseReactorClient());
        multiCommandChain.add(new RegisterCallbackServerAtGiraServer(IP_OF_CALLBACK_SERVER), new ResponseReactorGiraCallbackServer());
        multiCommandChain.add(new UIConfigCommand(), new ResponseReactorUIConfig());
        serverHandler.sendRequest(multiCommandChain);
    }

    /**
     * Establishes a connection to the callbackserver.
     */
    public void connectToCallbackServer(){
        setCallbackServerConnectionStatus(ServerConnectionEvent.CALLBACK_CONNECTION_ACTIVE);
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
        multiCommandChain.add(new RegisterCallback(IP_OF_CALLBACK_SERVER), new ResponseReactorCallbackServer());
        getAdditionalConfigs(multiCommandChain);
        serverHandler.sendRequest(multiCommandChain);
    }

    public void setServerConnectionStatus(boolean status) {
        serverConnectionStatus.postValue(status);
    }

    public MutableLiveData<Boolean> getServerConnectionStatus() {
        return serverConnectionStatus;
    }

    public void setCallbackServerConnectionStatus(ServerConnectionEvent callbackServerConnectionStatus) {
        this.callbackServerConnectionStatus = callbackServerConnectionStatus;
    }

    public void setGiraServerConnectionStatus(ServerConnectionEvent giraServerConnectionStatus) {
        this.giraServerConnectionStatus = giraServerConnectionStatus;
    }

    public ServerConnectionEvent getCallbackServerConnectionStatus() {
        return callbackServerConnectionStatus;
    }

    public ServerConnectionEvent getGiraServerConnectionStatus() {
        return giraServerConnectionStatus;
    }

    public void setLoginStatus(boolean status) {
        requestStatusLoginUser.postValue(status);
    }

    public LiveData<Boolean> getLoginStatus() {
        return requestStatusLoginUser;
    }

    private void getAdditionalConfigs(MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfig.CHANNEL), new ResponseReactorChannelConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfig.LOCATION), new ResponseReactorBeaconLocations());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfig.BOUNDARIES), new ResponseReactorBoundariesConfig());
    }

    /**
     * Requests the current uiconfig from the gira server.
     */
    public void requestOnlyUIConfig(){
        SingleReactorCommandChainImpl singleCommandChain = new SingleReactorCommandChainImpl(new ResponseReactorUIConfig());
        singleCommandChain.add(new UIConfigCommand());
        serverHandler.sendRequest(singleCommandChain);
    }

    /**
     * Requests the current channelconfig, beaconlocations, and boundaryconfig from the callbackserver.
     */
    public void requestOnlyAdditionalConfigs() {
        MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
        getAdditionalConfigs(multiCommandChain);
        serverHandler.sendRequest(multiCommandChain);
    }

    private void requestUnregisterClient() {
        AsyncCommand register = new UnRegisterAtCallbackServer(IP_OF_CALLBACK_SERVER);
        serverHandler.sendRequest(register);
    }

    /**
     * Requests a given value to be set by the gira server.
     * @param id ID of the datapoint
     * @param value Value of the datapoint that will be set
     */
    public void requestSetValue(String id, String value) {
        Command setValueCommand = new ChangeValueCommand(id, Float.parseFloat(value));
        serverHandler.sendRequest(setValueCommand);
    }

    /**
     * Requests the current status values of the given datapoint ids the gira server.
     * @param ids List containing the ids of the datapoints
     */
    public synchronized void requestGetValue(List<String> ids) {
        statusListSize = ids.size();
        newStatusValuesMap.clear();
        for(String id :ids) {
            Command getValueCommand = new GetValueCommand(id);
            handleResponseGetValue(serverHandler.sendRequest(getValueCommand));
        }
    }

    private void requestUnregisterCallbackServerAtGiraServer() {
        Command unregisterAtGira = new UnRegisterCallbackServerAtGiraServer();
        serverHandler.sendRequest(unregisterAtGira);
    }

    private synchronized void handleResponseGetValue(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received");
                Log.d(TAG, responseEntity.getBody().toString());

                GetValueResponse valueResponse = (GetValueResponse) responseEntity.getBody();
                String value = valueResponse.getValues().get(0).getValue();
                String uID = valueResponse.getValues().get(0).getUid();

                newStatusValuesMap.put(uID, value);
                if(statusListSize == newStatusValuesMap.size()) {
                    Map<String, String> updateValueMap =  newStatusValuesMap.entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    Repository.getInstance().setStatusGetValueMap(updateValueMap);
                }
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
            }
        } catch (Exception e) {
            Log.d(TAG, "handleResponseEntities, Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Unregisters the application from the gira server and the callbackserver.
     */
    public void unregisterFromServers() {
        requestUnregisterClient();
        requestUnregisterCallbackServerAtGiraServer();
    }

    /**
     * Gets the saved user credentials form the google password manager and starts the connection to the gira server on successful retrieval.
     */
    public void requestSavedCredentialsForLoginAtGira() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        if(parentApplication != null){
            CredentialsClient credentialsClient = Credentials.getClient(parentApplication);
            credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    addToExecutorService(new Thread(() -> connectToGira(task.getResult().getCredential().getId(),task.getResult().getCredential().getPassword())));
                }else{
                    toastUtility.prepareToast("Not able to retrieve Login data from Google.");
                }
            });
        }
    }
}