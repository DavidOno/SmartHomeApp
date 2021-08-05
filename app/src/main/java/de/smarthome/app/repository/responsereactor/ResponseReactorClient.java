package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorClient implements ResponseReactor {
    private static final String TAG = "ResponseReactorClient";
    private Repository repository;
    private ToastUtility toastUtility;

    public ResponseReactorClient() {
        this.repository = Repository.getInstance();
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, responseEntity.getBody().toString());
                Log.d(TAG, "No Problems when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                repository.serverConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_SUCCESS);
                repository.setLoginStatus(true);
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                repository.serverConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
                repository.setLoginStatus(false);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.serverConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            toastUtility.prepareToast("Unable to register User at Gira!");
            e.printStackTrace();
        }
    }
}