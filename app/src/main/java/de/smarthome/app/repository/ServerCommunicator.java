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

import de.smarthome.SmartHomeApplication;
import de.smarthome.app.model.responses.GetValueResponse;
import de.smarthome.app.repository.responsereactor.ResponseReactorBeaconConfig;
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

public class ServerCommunicator {
    private static final String TAG = "ServerCommunicator";
    private ToastUtility toastUtility = ToastUtility.getInstance();

    private static final String IP_OF_CALLBACK_SERVER = "192.168.132.222:9090";
    private final ServerHandler serverHandler;

    private MutableLiveData<Boolean> requestStatusLoginUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> serverConnectionStatus = new MutableLiveData<>();
    private ServerConnectionEvent callbackServerConnectionStatus;
    private ServerConnectionEvent giraServerConnectionStatus;

    private Application parentApplication = null;
    private int statusListSize = 0;
    private Map<String, String> newStatusValuesMap = new LinkedHashMap<>();

    public ServerCommunicator(@NonNull ServerHandler serverHandler){
        this.serverHandler = serverHandler;
    }

    public void setParentApplication(Application parentApplication) {
        this.parentApplication = parentApplication;
    }

    private void addToExecutorService(Thread newThread) {
        SmartHomeApplication.EXECUTOR_SERVICE.execute(newThread);
    }

    public void serverConnectionEvent(ServerConnectionEvent event){
        switch(event){
            case CALLBACK_CONNECTION_FAIL:
                if(callbackServerConnectionStatus != ServerConnectionEvent.CALLBACK_CONNECTION_FAIL)
                    setCallbackServerConnectionStatus(event);
                setServerConnectionStatus(false);
                break;
            case CALLBACK_CONNECTION_SUCCESS:
                if(callbackServerConnectionStatus != ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS){
                    toastUtility.prepareToast("Successfully connected to CallbackServer");
                    setCallbackServerConnectionStatus(event);
                }
                break;
            case GIRA_CONNECTION_FAIL:
                if(giraServerConnectionStatus != ServerConnectionEvent.GIRA_CONNECTION_FAIL)
                    setGiraServerConnectionStatus(event);
                setServerConnectionStatus(false);
                break;
            case GIRA_CONNECTION_SUCCESS:
                if(giraServerConnectionStatus != ServerConnectionEvent.GIRA_CONNECTION_SUCCESS){
                    toastUtility.prepareToast("Successfully connected to Gira.");
                    setGiraServerConnectionStatus(event);
                }
                break;
        }
    }

    public void retryConnectionToServer(){
        if(giraServerConnectionStatus == ServerConnectionEvent.GIRA_CONNECTION_FAIL){
            getSavedCredentialsForLoginAtGira();
        }
        if(callbackServerConnectionStatus == ServerConnectionEvent.CALLBACK_CONNECTION_FAIL){
            connectToCallbackServer();
        }
    }

    public void connectToGira(String userName, String pwd){
        Thread connectToGiraThread = new Thread(() -> {
            setGiraServerConnectionStatus(ServerConnectionEvent.GIRA_CONNECTION_ACTIVE);
            MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
            multiCommandChain.add(new RegisterClientCommand(userName, pwd), new ResponseReactorClient());
            multiCommandChain.add(new RegisterCallbackServerAtGiraServer(IP_OF_CALLBACK_SERVER), new ResponseReactorGiraCallbackServer());
            multiCommandChain.add(new UIConfigCommand(), new ResponseReactorUIConfig());
            serverHandler.sendRequest(multiCommandChain);
        });
        addToExecutorService(connectToGiraThread);
    }

    public void connectToCallbackServer(){
        Thread connectToCallbackServerThread = new Thread(() -> {
            setCallbackServerConnectionStatus(ServerConnectionEvent.CALLBACK_CONNECTION_ACTIVE);
            MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
            multiCommandChain.add(new RegisterCallback(IP_OF_CALLBACK_SERVER), new ResponseReactorCallbackServer());
            getAdditionalConfigs(multiCommandChain);
            serverHandler.sendRequest(multiCommandChain);
        });
        addToExecutorService(connectToCallbackServerThread);
    }

    public void setServerConnectionStatus(Boolean status) {
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

    public void setLoginStatus(Boolean status) {
        requestStatusLoginUser.postValue(status);
    }

    public LiveData<Boolean> getLoginStatusForUser() {
        return requestStatusLoginUser;
    }

    private void getAdditionalConfigs(MultiReactorCommandChainImpl multiCommandChain) {
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfig.CHANNEL), new ResponseReactorChannelConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfig.LOCATION), new ResponseReactorBeaconConfig());
        multiCommandChain.add(new AdditionalConfigCommand(IP_OF_CALLBACK_SERVER, AdditionalConfig.BOUNDARIES), new ResponseReactorBoundariesConfig());
    }

    public void requestOnlyUIConfig(){
        Thread requestOnlyUIConfigThread = new Thread(() -> {
            SingleReactorCommandChainImpl singleCommandChain = new SingleReactorCommandChainImpl(new ResponseReactorUIConfig());
            singleCommandChain.add(new UIConfigCommand());
            serverHandler.sendRequest(singleCommandChain);
        });
        addToExecutorService(requestOnlyUIConfigThread);
    }

    public void requestOnlyAdditionalConfigs() {
        Thread requestOnlyAdditionalConfigsThread = new Thread(() -> {
            MultiReactorCommandChainImpl multiCommandChain = new MultiReactorCommandChainImpl();
            getAdditionalConfigs(multiCommandChain);
            serverHandler.sendRequest(multiCommandChain);
        });
        addToExecutorService(requestOnlyAdditionalConfigsThread);
    }

    private void requestUnregisterClient() {
        Thread requestUnregisterClientThread = new Thread(() -> {
            AsyncCommand register = new UnRegisterAtCallbackServer(IP_OF_CALLBACK_SERVER);
            serverHandler.sendRequest(register);
        });
        addToExecutorService(requestUnregisterClientThread);
    }

    public void requestSetValue(String id, String value) {
        Thread requestSetValueThread = new Thread(() -> {
            Command setValueCommand = new ChangeValueCommand(id, Float.parseFloat(value));
            serverHandler.sendRequest(setValueCommand);
        });
        addToExecutorService(requestSetValueThread);
    }

    public synchronized void requestGetValue(List<String> ids) {
        statusListSize = ids.size();
        newStatusValuesMap.clear();
        //InternalStorageWriter.writeFileOnInternalStorage(parentApplication.getApplicationContext(),
        //        "GIRA", "4.2 SC RequestGetValue, size: " + statusListSize + "\n");
        Thread requestGetValueThread = new Thread(() -> {
            for(String id :ids) {
                Command getValueCommand = new GetValueCommand(id);
                handleResponseGetValue(serverHandler.sendRequest(getValueCommand));
            }
        });
        addToExecutorService(requestGetValueThread);
    }

    private void requestUnregisterCallbackServerAtGiraServer() {
        Thread requestUnRegisterCallbackServerAtGiraServerThread = new Thread(() -> {
            Command unregisterAtGira = new UnRegisterCallbackServerAtGiraServer();
            serverHandler.sendRequest(unregisterAtGira);
        });
        addToExecutorService(requestUnRegisterCallbackServerAtGiraServerThread);
    }

    private synchronized void handleResponseGetValue(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received");
                Log.d(TAG, responseEntity.getBody().toString());
                GetValueResponse valueResponse = (GetValueResponse) responseEntity.getBody();

                String value = valueResponse.getValues().get(0).getValue();
                String uID = valueResponse.getValues().get(0).getUid();
                //InternalStorageWriter.writeFileOnInternalStorage(parentApplication.getApplicationContext(),
                //       "GIRA", "SC handleResponseGetValue, uID: " + uID + " value " + value + "\n");

                newStatusValuesMap.put(uID, value);
                //InternalStorageWriter.writeFileOnInternalStorage(parentApplication.getApplicationContext(),
                //        "GIRA", "SC handleResponseGetValue, statusListSize: " + statusListSize + " MapSize " + newStatusValuesMap.size() + "\n\n");
                if(statusListSize == newStatusValuesMap.size()) {
                    Repository.getInstance().setStatusGetValueMap(newStatusValuesMap);
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

    public void unsubscribeFromEverything() {
        requestUnregisterClient();
        requestUnregisterCallbackServerAtGiraServer();
    }

    public void getSavedCredentialsForLoginAtGira() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        if(parentApplication != null){
            CredentialsClient credentialsClient = Credentials.getClient(parentApplication);
            credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    connectToGira(task.getResult().getCredential().getId(),task.getResult().getCredential().getPassword());
                }else{
                    toastUtility.prepareToast("Not able to retrieve Login data from Google.");
                }
            });
        }
    }
}