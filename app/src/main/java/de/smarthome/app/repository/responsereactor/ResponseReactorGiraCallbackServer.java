package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorGiraCallbackServer implements ResponseReactor {
    private static final String TAG = "ResponseReactorGiraCallbackServer";
    private ToastUtility toastUtility;
    private Repository repository;

    public ResponseReactorGiraCallbackServer() {
        repository = Repository.getInstance();
        toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, "No Problems when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());
                repository.serverConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_SUCCESS);
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());
                toastUtility.prepareToast("Unable to reach Gira Server");
                repository.serverConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            toastUtility.prepareToast("Exception: Unable to register CallbackServer at Gira!");
            repository.serverConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }
}